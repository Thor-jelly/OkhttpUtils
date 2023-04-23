package com.jelly.thor.example.netserver

import android.net.ParseException
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.TypeReference
import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.jelly.thor.okhttputils.callback.IParseData
import com.jelly.thor.okhttputils.model.ResponseModel
import com.jelly.thor.okhttputils.utils.ErrorCode
import com.jelly.thor.okhttputils.utils.GsonTypes
import com.jushuitan.jht.basemodule.utils.net.exception.ResponseException
import com.jushuitan.jht.basemodule.utils.net.exception.ServerException
import okhttp3.Request
import okhttp3.Response
import org.apache.http.conn.ConnectTimeoutException
import org.json.JSONException
import java.lang.reflect.ParameterizedType
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.nio.charset.Charset
import javax.net.ssl.SSLHandshakeException

/**
 * 创建人：吴冬冬<br/>
 * 创建时间：2022/12/5 14:34 <br/>
 */
class ParseDataImpl : IParseData {
    override fun <T> parseData(
        id: Int,
        response: Response,
        clazz: Class<T>?,
        parameterizedTypeImpl: ParameterizedType?
    ): T {
        //是否需要转换数据
        val isDataConversion = (clazz != null && response.javaClass != clazz) || parameterizedTypeImpl != null
        //网络请求码错误
        if (!response.isSuccessful) {
            throw ServerException(ErrorCode.NET_ERROR, "服务器请求失败！")
        }

        if (response.body == null) {
            throw ServerException(ErrorCode.NET_ERROR, "服务器请求失败！")
        }
        val responseStr = if (isDataConversion) {
            response.body!!.string()
        } else {
            val source = response.body!!.source()
            source.request(Long.MAX_VALUE)
            source.buffer.clone().readString(Charset.defaultCharset())
        }
        if (responseStr.contains("\"code\"")) {
            //返回
            val model = if (parameterizedTypeImpl != null) {
                try {
                    //fastjson写法
                    JSON.parseObject(responseStr, parameterizedTypeImpl) as ResponseModel<*>
//                    Gson().fromJson<ResponseModel<T>>(responseStr, parameterizedTypeImpl as GsonTypes.ParameterizedTypeImpl)
                } catch (e: Exception) {
                    throw ResponseException(ErrorCode.PARSE_ERROR, e, "解析错误")
                }
            } else {
                try {
                    //fastjson写法
                    JSON.parseObject(
                        responseStr,
                        object : TypeReference<ResponseModel<T>>(clazz) {}) as ResponseModel<T>
                    //gson
//                    val fromJson = Gson().fromJson<ResponseModel<T>>(
//                        responseStr,
//                        GsonTypes.ParameterizedTypeImpl(null, ResponseModel::class.java, clazz)
//                    )
                } catch (e: Exception) {
                    throw ResponseException(ErrorCode.PARSE_ERROR, e, "解析错误")
                }
            }
            if (model.isSuccess()) {
                return if (clazz != null && parameterizedTypeImpl != null) {
                    model as T
                } else {
                    model.data as T
                }
            } else {
                throw ServerException(model.code, model.msg)
            }
        }

        return response as T
    }

    override fun handleError(it: Throwable, request: Request?): ResponseException {
        return when (it) {
            /*is HttpException -> {
                val responseException = ResponseException(ErrorCode.HTTP_ERROR, it)
                responseException.message = it.message()
                responseException
            }*/
            is ResponseException -> {
//                if (request != null) {
//                    NetPointEvent.addRequestErrorEvent(
//                        request,
//                        request.url.toString(),
//                        "错误码：${it.code}；错误信息：${it.message} ${it.throwable}"
//                    )
//                }
                it
            }

            is ServerException -> {
                if (request != null) {
//                    NetPointEvent.addRequestBieErrorEvent(
//                        request,
//                        request.url.toString(),
//                        "错误码：${it.code}；错误信息：${it.message}"
//                    )
                }
//                if (ErrorCode.NEED_LOGIN == it.code) {
//                    gotoLoginActivity(it)
//                } else {
                val responseException = ResponseException(it.code, it)
                responseException.message = it.message
                responseException
//                }
            }

            is JsonParseException,
            is JSONException,
            is ParseException -> {
                if (request != null) {
//                    NetPointEvent.addRequestErrorEvent(
//                        request,
//                        request.url.toString(),
//                        "解析错误=${it}"
//                    )
                }
                ResponseException(ErrorCode.PARSE_ERROR, it, "解析错误")
            }

            is ConnectException -> {
                if (request != null) {
//                    NetPointEvent.addRequestErrorEvent(
//                        request,
//                        request.url.toString(),
//                        "连接失败=${it}"
//                    )
                }
                ResponseException(ErrorCode.NET_ERROR, it, "连接失败")
            }

            is SSLHandshakeException -> {
                if (request != null) {
//                    NetPointEvent.addRequestErrorEvent(
//                        request,
//                        request.url.toString(),
//                        "证书验证失败=${it}"
//                    )
                }
                ResponseException(ErrorCode.SSL_ERROR, it, "证书验证失败")
            }

            is ConnectTimeoutException,
            is SocketTimeoutException -> {
                if (request != null) {
//                    NetPointEvent.addRequestErrorEvent(
//                        request,
//                        request.url.toString(),
//                        "连接超时=${it}"
//                    )
                }
                ResponseException(ErrorCode.TIMEOUT_ERROR, it, "连接超时")
            }
            /*is MalformedJsonException -> {
                gotoLoginActivity(it)
            }*/
            else -> {
                if (request != null) {
//                    NetPointEvent.addRequestErrorEvent(
//                        request,
//                        request.url.toString(),
//                        "未知错误=${it}"
//                    )
                }
                ResponseException(ErrorCode.UNKNOWN, it, "未知错误，请重启APP")
            }
        }
    }

//    /**
//     * 跳转到登录界面
//     */
//    private fun gotoLoginActivity(it: Throwable): ResponseException {
//        OkHttpUtils.getInstance().cancelAll()
//        CacheCleanManager.clearLogoutInfo()
//        ActivityUtils.finishAllActivity()
//        val forName = Class.forName(".LoginActivity")
//        val intent = Intent(BaseApplication.getAppContext(), forName)
//        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
//        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//        BaseApplication.getAppContext().startActivity(intent)
//        return ResponseException(ErrorCode.NEED_LOGIN, it, "用户登录信息已失效，请重新登录")
//    }
}