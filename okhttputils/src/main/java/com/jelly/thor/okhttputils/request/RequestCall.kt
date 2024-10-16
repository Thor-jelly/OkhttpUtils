package com.jelly.thor.okhttputils.request

import android.text.TextUtils
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
import io.reactivex.rxjava3.functions.Action
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.channels.onSuccess
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.isActive
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.HttpUrl
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.lang.Exception
import java.lang.IllegalArgumentException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * 类描述：网络请求<br></br>
 * 创建人：吴冬冬<br></br>
 * 创建时间：2018/5/15 17:27 <br></br>
 */
class RequestCall internal constructor(okHttpRequest: OkHttpRequest) {
    private val mOkHttpRequest: OkHttpRequest

    init {
        this.mOkHttpRequest = okHttpRequest
    }

    private var mCall: Call? = null

    var mRequest: Request? = null
        private set

    /**
     * flow模式
     */
    inline fun <reified T : Any> asFlow() {
        flow()
            .flowOn(Dispatchers.Main)
            .map {
                getModel<T>(it)
            }.catch {
                val error = ParseDataUtils.handleError(it, mRequest)
                throw error
            }.flowOn(Dispatchers.Default)
    }

    /**
     * flow模式
     */
    fun flow(): Flow<Response> {
        return callbackFlow<Response> {
            execute<Response>(object : ConverterCallback() {
                override fun onError(e: kotlin.Exception) {
                    if (!this@callbackFlow.isActive) {
                        return
                    }
                    close(e)
                }

                override fun onSuccess(response: Response) {
                    if (!this@callbackFlow.isActive) {
                        return
                    }
                    trySendBlocking(response)
                        .onSuccess {
                            close()
                        }.onFailure {
                            close(it)
                        }
                }
            }, null)

            awaitClose {
                mCall?.run {
                    if (this.isCanceled()) {
                        return@run
                    }
                    this.cancel()
                }
            }
        }
    }

    /**
     * 协程模式
     */
    suspend inline fun <reified T : Any> asCoroutines(): T {
        return try {
            val response = coroutines()
            withContext(Dispatchers.Default) {
                getModel<T>(response)
            }
        } catch (e: kotlin.Exception) {
            val error = ParseDataUtils.handleError(e, mRequest)
            throw error
        }
    }

    inline fun <reified T : Any> getModel(response: Response): T {
        val converterFactory = OkHttpUtils.getInstance().converterFactory
        val parseData = converterFactory.responseBodyConverter(0, object : RefParamsType<T>() {}, response.request, response, T::class.java)
        return parseData
    }

    /**
     * 协程模式
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
                mCall?.run {
                    if (this.isCanceled()) {
                        return@run
                    }
                    this.cancel()
                }
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
     * rxJava模式
     */
    fun rxJava(): Maybe<Response> {
        return Maybe.create<Response> { emitter ->
            if (emitter.isDisposed) {
                return@create
            }
            execute<Response>(object : ConverterCallback() {
                override fun onError(e: kotlin.Exception) {
                    emitter.onError(e)
                }

                override fun onSuccess(response: Response) {
                    emitter.onSuccess(response)
                }
            }, null)
        }.doOnDispose(object : Action {
            override fun run() {
                mCall?.run {
                    if (this.isCanceled()) {
                        return@run
                    }
                    this.cancel()
                }
            }
        })
    }

    /**
     * java模式
     */
    fun <T> execute(callback: Callback<T?>?) {
        execute(null, callback)
    }

    private fun <T> execute(converterCallback: ConverterCallback?, callback: Callback<T?>?) {
        //判断是否有网络
        val isNet = CommontUtils.networkAvailable()
        if (!isNet) {
            val errorStr = "当前没有网络！"
            if (converterCallback == null) {
                callback!!.mOkHttpRequest = mOkHttpRequest
                //失败回调 主线程中
                sendOkHttpFail<T?>(mOkHttpRequest.okHttpRequestBuilder.getId(), ErrorCode.NET_ERROR, errorStr, callback)
            } else {
                converterCallback.onError(ServerException(ErrorCode.NET_ERROR, errorStr))
            }
            return
        }

        //回调通知开始
        if (callback != null) {
            callback.mOkHttpRequest = mOkHttpRequest
            Platform.get().execute(object : Runnable {
                override fun run() {
                    callback.onBefore(mOkHttpRequest.okHttpRequestBuilder.getId())
                }
            })
        }

        val okHttpClient = OkHttpUtils.getInstance().getOkHttpClient()

        try {
            mRequest = mOkHttpRequest.getRequest()
        } catch (e: IllegalArgumentException) {
            //失败回调 主线程中
            val errorStr = mOkHttpRequest.url.toString() + "添加参数异常 " + e.message
            if (converterCallback == null) {
                sendOkHttpFail<T?>(mOkHttpRequest.okHttpRequestBuilder.getId(), ErrorCode.PARAMS_EXCEPTION, errorStr, callback)
            } else {
                converterCallback.onError(ServerException(ErrorCode.PARAMS_EXCEPTION, errorStr))
            }
            return
        }

        //动态追加的公共参数
        val changeCommonParameters = callback?.addChangeCommonParameters()
        if (changeCommonParameters != null && !changeCommonParameters.isEmpty()) {
            val method = mRequest!!.method
            //            switch (method) {
//                case "POST":
//                    RequestBody body = request.body();
//                    if (body instanceof FormBody) {
//                        //键值对形式
//                        FormBody.Builder newFormBody = new FormBody.Builder();
//                        FormBody oldFormBody = (FormBody) body;
//                        //把旧的添加进新的
//                        for (int i = 0; i < oldFormBody.size(); i++) {
//                            newFormBody.add(oldFormBody.encodedName(i), oldFormBody.encodedValue(i));
//                        }
//                        //添加新的
//                        for (Map.Entry<String, String> entry : changeCommonParameters.entrySet()) {
//                            newFormBody.add(entry.getKey(), entry.getValue());
//                        }
//                        //重新创建一个新的request
//                        FormBody build = newFormBody.build();
//                        Request newRequest = request.newBuilder()
//                                .post(build)
//                                .build();
//                        mCall = okHttpClient.newCall(newRequest);
//                    } else {
//                        //暂时只有json格式
//
//                    }
//                    break;
//                default:
            val newUrl: HttpUrl.Builder = mRequest!!.url.newBuilder()
            for (entry in changeCommonParameters.entries) {
                val key: String = entry.key!!
                val value = entry.value
                if (value == null) {
                    sendOkHttpFail<T?>(mOkHttpRequest.okHttpRequestBuilder.getId(), ErrorCode.PARAMS_EXCEPTION, mRequest!!.url.toString() + "addChangeCommonParameters参数异常 " + "参数中的" + key + " 赋值为null", callback)
                    return
                }
                newUrl.addQueryParameter(key, value)
            }
            val newRequest: Request = mRequest!!.newBuilder().url(newUrl.build()).build()
            mCall = okHttpClient.newCall(newRequest)
            //                    break;
//            }
        } else {
            mCall = okHttpClient.newCall(mRequest!!)
        }


        //OkHttpUtils.getInstance().execute(this, callback);
        mCall?.enqueue(object : okhttp3.Callback {
            override fun onFailure(call: Call, e: IOException) {
                //在子线程中
//                if (BuildConfig.DEBUG) {
//                    Log.d("OkHttpUtils", mOkHttpRequest.getId() + "-OkHttp3--->>>onFailure: " + (e == null ? "IOException 为null" : e.toString()));
//                    Log.d("OkHttpUtils", mOkHttpRequest.getId() + "-OkHttp3--->>>onFailure: 当前网络是否被被取消=" + call.isCanceled());
//                }
                if (!call.isCanceled()) {
                    call.cancel()
                }
                if (converterCallback == null) {
                    //失败回调 主线程中
                    sendOkHttpFail<T?>(mOkHttpRequest.okHttpRequestBuilder.getId(), ErrorCode.NET_ERROR, "网络异常！", callback)
                } else {
                    converterCallback.onError(ServerException(ErrorCode.NET_ERROR, "网络异常！"))
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val id = mOkHttpRequest.okHttpRequestBuilder.getId()
                //在子线程中
                if (call.isCanceled()) {
                    val errorStr = "网络被取消！"
                    if (converterCallback == null) {
                        //失败回调 主线程中
                        sendOkHttpFail<T?>(id, ErrorCode.NET_CANCEL, errorStr, callback)
                    } else {
                        converterCallback.onError(ServerException(ErrorCode.NET_CANCEL, errorStr))
                    }
                    return
                }
                if (!response.isSuccessful) {
                    val code = response.code
                    var errorStr = response.message
                    if (TextUtils.isEmpty(errorStr)) {
                        errorStr = response.toString()
                    }
                    if (converterCallback == null) {
                        //失败回调 主线程中
                        sendOkHttpFail<T?>(id, code, errorStr, callback)
                    } else {
                        converterCallback.onError(ServerException(code, errorStr))
                    }
                    return
                }

                if (converterCallback == null) {
                    var o: Any? = null
                    try {
                        //数据解析需要在子线程
                        o = callback!!.parseNetworkResponse(response, id, mOkHttpRequest)
                    } catch (e: Exception) {
                        //e.printStackTrace();
                        val responseException = ParseDataUtils.handleError(e, response.request)
                        //失败回调 主线程中
                        sendOkHttpFail<T?>(id, responseException.code, responseException.message, callback)
                    } /*finally {
                        ResponseBody body = response.body();
                        if (body != null) {
                            body.close();
                        }
                    }*/

                    //如果自定义没有返回null，如果返回null表示不需要执行成功回调onResponse onAfter
                    //主线程中
                    if (o != null) {
                        sendOkHttpSuccess<T?>(id, o, response.request, callback)
                    } else {
                        sendOkHttpAfter<T?>(id, callback)
                    }
                } else {
                    converterCallback.onSuccess(response)
                }
            }
        })
    }

    private fun <T> sendOkHttpAfter(id: Int, callback: Callback<T?>?) {
        Platform.get().execute(object : Runnable {
            override fun run() {
                if (callback == null) {
                    return
                }
                callback.onAfter(id)
            }
        })
    }

    private fun <T> sendOkHttpFail(id: Int, code: Int, errorStr: String?, callback: Callback<T?>?) {
        Platform.get().execute(object : Runnable {
            override fun run() {
                if (callback == null) {
                    return
                }
                callback.onError(code, errorStr, id, mOkHttpRequest)
                callback.onAfter(id)
            }
        })
    }

    private fun <T> sendOkHttpSuccess(id: Int, o: Any?, request: Request?, callback: Callback<T?>?) {
        //下面两个方法需要转换到主线程
        //解析完成的数据
        Platform.get().execute(object : Runnable {
            override fun run() {
                if (callback == null) {
                    return
                }
                try {
                    callback.onResponse(0, o as T?, id)
                } catch (e: Exception) {
                    sendOkHttpFail<T?>(id, ErrorCode.HANDLE_SUCCESS_ERROR, "在处理数据中失败：" + e.message, callback)
                }
                //完成
                callback.onAfter(id)
            }
        })
    }
}
