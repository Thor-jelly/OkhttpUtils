package com.jelly.thor.okhttputils.request

import com.jelly.thor.okhttputils.OkHttpUtils
import com.jelly.thor.okhttputils.callback.Callback
import com.jelly.thor.okhttputils.callback.ConverterCallback
import com.jelly.thor.okhttputils.callback.ParseDataUtils
import com.jelly.thor.okhttputils.callback.dataConversion
import com.jelly.thor.okhttputils.converters.RefParamsType
import com.jelly.thor.okhttputils.exception.ServerException
import com.jelly.thor.okhttputils.utils.CommontUtils
import com.jelly.thor.okhttputils.utils.ErrorCode
import com.jelly.thor.okhttputils.utils.Platform
import io.reactivex.rxjava3.core.Maybe
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.lang.Exception
import java.lang.IllegalArgumentException
import java.util.concurrent.atomic.AtomicReference
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * 类描述：网络请求<br></br>
 * 创建人：吴冬冬<br></br>
 * 创建时间：2018/5/15 17:27 <br></br>
 */
class RequestCall internal constructor(okHttpRequest: OkHttpRequest) {
    private val mOkHttpRequest: OkHttpRequest = okHttpRequest

    private val mCallRef: AtomicReference<Call?> = AtomicReference(null)

    var mRequest: Request? = null
        private set

    /**
     * flow模式，返回解析后的模型
     */
    inline fun <reified T : Any> asFlow(): Flow<T> {
        return flow()
            .map { getModel<T>(it) }
            .flowOn(Dispatchers.Default)
            .catch {
                if (it is CancellationException) throw it
                val error = ParseDataUtils.handleError(it, mRequest)
                throw error
            }
    }

    /**
     * flow模式，返回原始Response
     */
    fun flow(): Flow<Response> {
        return flow {
            emit(coroutines())
        }
    }

    /**
     * 协程模式，返回解析后的模型
     */
    suspend inline fun <reified T : Any> asCoroutines(): T {
        return try {
            val response = coroutines()
            withContext(Dispatchers.Default) {
                getModel<T>(response)
            }
        } catch (e: CancellationException) {
            throw e  // 不拦截协程取消异常，保证结构化并发正常工作
        } catch (e: kotlin.Exception) {
            val error = ParseDataUtils.handleError(e, mRequest)
            throw error
        }
    }

    /**
     * 解析Response为指定类型
     */
    inline fun <reified T : Any> getModel(response: Response): T {
        val converterFactory = OkHttpUtils.getInstance().converterFactory
        val parseData = converterFactory.responseBodyConverter(
            0,
            object : RefParamsType<T>() {},
            response.request,
            response,
            T::class.java
        )
        return parseData
    }

    /**
     * 协程模式，返回原始Response
     */
    suspend fun coroutines(): Response {
        return suspendCancellableCoroutine { continuation ->
            execute<Response>(object : ConverterCallback() {
                override fun onError(e: kotlin.Exception) {
                    if (!continuation.isActive) {
                        return
                    }
                    continuation.resumeWithException(e)
                }

                override fun onSuccess(response: Response) {
                    if (!continuation.isActive) {
                        return
                    }
                    continuation.resume(response)
                }
            }, null)

            //支持协程取消请求
            continuation.invokeOnCancellation {
                cancelCall()
            }
        }
    }

    /**
     * rxJava模式转换成对应model
     */
    inline fun <reified T : Any> asRxJava(): Maybe<T> {
        return this.rxJava()
            .dataConversion<T>()
    }

    /**
     * rxJava模式，返回原始Response
     */
    fun rxJava(): Maybe<Response> {
        return Maybe.create<Response> { emitter ->
            if (emitter.isDisposed) {
                return@create
            }
            execute<Response>(object : ConverterCallback() {
                override fun onError(e: kotlin.Exception) {
                    if (!emitter.isDisposed) {
                        emitter.onError(e)
                    }
                }

                override fun onSuccess(response: Response) {
                    if (!emitter.isDisposed) {
                        emitter.onSuccess(response)
                    }
                }
            }, null)
        }.doOnDispose {
            cancelCall()
        }
    }

    /**
     * 取消网络请求（线程安全）
     */
    private fun cancelCall() {
        mCallRef.get()?.let { call ->
            if (!call.isCanceled()) {
                call.cancel()
            }
        }
    }

    /**
     * java模式
     */
    fun <T> execute(callback: Callback<T?>?) {
        execute(null, callback)
    }

    private fun <T> execute(converterCallback: ConverterCallback?, callback: Callback<T?>?) {
        //判断是否有网络（在后台线程检查，避免阻塞主线程）
        val isNet = CommontUtils.networkAvailable()
        if (!isNet) {
            handleError(ErrorCode.NET_ERROR, "当前没有网络！", converterCallback, callback)
            return
        }

        //回调通知开始
        notifyBefore(callback)

        val okHttpClient = OkHttpUtils.getInstance().getOkHttpClient()

        //构建请求
        val request = buildRequest(converterCallback, callback) ?: return
        mRequest = request

        //处理动态追加的公共参数
        val finalRequest = applyChangeCommonParameters(request, callback, okHttpClient) ?: return

        //执行网络请求
        executeRequest(finalRequest, okHttpClient, converterCallback, callback)
    }

    /**
     * 构建请求
     */
    private fun <T> buildRequest(converterCallback: ConverterCallback?, callback: Callback<T?>?): Request? {
        return try {
            mOkHttpRequest.getRequest()
        } catch (e: IllegalArgumentException) {
            val errorStr = "${mOkHttpRequest.url}添加参数异常 ${e.message}"
            handleError(ErrorCode.PARAMS_EXCEPTION, errorStr, converterCallback, callback)
            null
        }
    }

    /**
     * 应用动态追加的公共参数
     */
    private fun <T> applyChangeCommonParameters(
        request: Request,
        callback: Callback<T?>?,
        okHttpClient: okhttp3.OkHttpClient
    ): Request? {
        val changeCommonParameters = callback?.addChangeCommonParameters()
        if (changeCommonParameters.isNullOrEmpty()) {
            mCallRef.set(okHttpClient.newCall(request))
            return request
        }

        val newUrl = request.url.newBuilder()
        for (entry in changeCommonParameters.entries) {
            val key = entry.key
            val value = entry.value
            if (key == null || value == null) {
                val errorStr = "${request.url}addChangeCommonParameters参数异常 参数中的$key 赋值为null"
                handleError(ErrorCode.PARAMS_EXCEPTION, errorStr, null, callback)
                return null
            }
            newUrl.addQueryParameter(key, value)
        }
        val newRequest = request.newBuilder().url(newUrl.build()).build()
        mCallRef.set(okHttpClient.newCall(newRequest))
        return newRequest
    }

    /**
     * 执行网络请求
     */
    private fun <T> executeRequest(
        request: Request,
        okHttpClient: okhttp3.OkHttpClient,
        converterCallback: ConverterCallback?,
        callback: Callback<T?>?
    ) {
        mCallRef.get()?.enqueue(object : okhttp3.Callback {
            override fun onFailure(call: Call, e: IOException) {
                //在子线程中
                if (!call.isCanceled()) {
                    call.cancel()
                }
                handleError(ErrorCode.NET_ERROR, "网络异常！", converterCallback, callback)
            }

            override fun onResponse(call: Call, response: Response) {
                val id = mOkHttpRequest.okHttpRequestBuilder.getId()
                //在子线程中
                if (call.isCanceled()) {
                    handleError(ErrorCode.NET_CANCEL, "网络被取消！", converterCallback, callback)
                    return
                }
                if (!response.isSuccessful) {
                    val code = response.code
                    val errorStr = response.message.ifEmpty { response.toString() }
                    handleError(code, errorStr, converterCallback, callback)
                    return
                }

                if (converterCallback == null) {
                    handleSuccessResponse(response, id, callback)
                } else {
                    converterCallback.onSuccess(response)
                }
            }
        })
    }

    /**
     * 处理成功响应
     */
    private fun <T> handleSuccessResponse(
        response: Response,
        id: Int,
        callback: Callback<T?>?
    ) {
        var result: Any? = null
        try {
            //数据解析需要在子线程
            result = callback?.parseNetworkResponse(response, id, mOkHttpRequest)
        } catch (e: Exception) {
            val responseException = ParseDataUtils.handleError(e, response.request)
            sendOkHttpFail(id, responseException.code, responseException.message, callback)
            return
        }

        //如果自定义没有返回null，如果返回null表示不需要执行成功回调onResponse onAfter
        if (result != null) {
            sendOkHttpSuccess(id, result, response.request, callback)
        } else {
            sendOkHttpAfter(id, callback)
        }
    }

    /**
     * 通知请求开始
     */
    private fun notifyBefore(callback: Callback<*>?) {
        if (callback != null) {
            callback.mOkHttpRequest = mOkHttpRequest
            Platform.get().execute {
                callback.onBefore(mOkHttpRequest.okHttpRequestBuilder.getId())
            }
        }
    }

    /**
     * 统一错误处理
     */
    private fun <T> handleError(
        code: Int,
        errorStr: String,
        converterCallback: ConverterCallback?,
        callback: Callback<T?>?
    ) {
        if (converterCallback == null) {
            if (callback != null) {
                callback.mOkHttpRequest = mOkHttpRequest
            }
            sendOkHttpFail(mOkHttpRequest.okHttpRequestBuilder.getId(), code, errorStr, callback)
        } else {
            converterCallback.onError(ServerException(code, errorStr))
        }
    }

    /**
     * 发送请求完成回调
     */
    private fun <T> sendOkHttpAfter(id: Int, callback: Callback<T?>?) {
        if (callback == null) return
        Platform.get().execute {
            callback.onAfter(id)
        }
    }

    /**
     * 发送请求失败回调
     */
    private fun <T> sendOkHttpFail(id: Int, code: Int, errorStr: String?, callback: Callback<T?>?) {
        if (callback == null) return
        Platform.get().execute {
            callback.onError(code, errorStr, id, mOkHttpRequest)
            callback.onAfter(id)
        }
    }

    /**
     * 发送请求成功回调
     */
    private fun <T> sendOkHttpSuccess(id: Int, o: Any?, request: Request?, callback: Callback<T?>?) {
        if (callback == null) return
        Platform.get().execute {
            try {
                @Suppress("UNCHECKED_CAST")
                callback.onResponse(0, o as T?, id)
            } catch (e: Exception) {
                sendOkHttpFail(id, ErrorCode.HANDLE_SUCCESS_ERROR, "在处理数据中失败：${e.message}", callback)
            }
            callback.onAfter(id)
        }
    }
}
