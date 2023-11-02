package com.jelly.thor.example.netserver

import android.net.ParseException
import com.google.gson.JsonParseException
import com.jelly.thor.okhttputils.OkHttpUtils
import com.jelly.thor.okhttputils.callback.IParseData
import com.jelly.thor.okhttputils.exception.ResponseException
import com.jelly.thor.okhttputils.exception.ServerException
import com.jelly.thor.okhttputils.model.ResponseModel
import com.jelly.thor.okhttputils.utils.ErrorCode
import okhttp3.Request
import okhttp3.Response
import org.apache.http.conn.ConnectTimeoutException
import org.json.JSONException
import java.io.InterruptedIOException
import java.lang.reflect.Type
import java.net.ConnectException
import java.net.SocketException
import java.net.SocketTimeoutException
import javax.net.ssl.SSLException
import javax.net.ssl.SSLHandshakeException

/**
 * 创建人：吴冬冬<br/>
 * 创建时间：2022/12/5 14:34 <br/>
 */
class ParseDataImpl : IParseData {
    override fun <T> parseData(
        id: Int,
        response: Response,
        clazz: Class<*>?,
        parameterizedTypeImpl: Type?
    ): T {
        val responseStr = OkHttpUtils.getInstance().converterFactory.convertStr(
            id,
            response,
            clazz,
            parameterizedTypeImpl
        )
        if (responseStr.contains("\"code\"")) {
            //新网关返回
            val model = OkHttpUtils.getInstance().converterFactory.convert<T>(
                id,
                response,
                responseStr,
                clazz,
                parameterizedTypeImpl
            ) as ResponseModel<T>
//            val jsonType = object : TypeToken<ResponseModel<T>>() {}.type
//            val model = Gson().fromJson<ResponseModel<T>>(responseStr, jsonType)
            if (model.isSuccess() || model.msg.isNullOrEmpty()) {
                /*if (model.cookie != null) {
                    //处理cookie
                    handleCookie(model)
                }*/
                //NetPointEvent.addRequestEndEvent(response.request, model, model.traceId, model.requestId)
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

    //新网关处理cookie
//    fun <T> handleCookie(model: ResponseModel<T>) {
//        val cookies = model.getCookies()
//        if (cookies.isNullOrEmpty()) {
//            return
//        }
//        val url = SPCookieStore.COOK_HOST.toHttpUrlOrNull()
//        //UserInfoManager.clean()
//
//        CookieJarImpl.getInstance().cookieStore.saveCookie(url, cookies)
//        for (cookie in cookies) {
//            //Timber.tag("123===").d("获取到的cookie=${cookie.toString()}")
//            //处理用户数据
//            val cookieName = cookie.name
//            //token
//            if (cookieName.contains("u_sso_token")) {
//                UserInfoManager.updateToken(cookie.value)
//            }
//            //登录id
//            if (cookieName.contains("u_lid")) {
//                UserInfoManager.updateULId(cookie.value)
//            }
//            //用户id
//            if (cookieName.contains("u_id")) {
//                UserInfoManager.updateUId(cookie.value)
//            }
//            //用户名
//            if (cookieName.contains("u_name")) {
//                UserInfoManager.updateUName(NetUtils.urlDecode(cookie.value))
//            }
//            //公司id
//            if (cookieName.contains("u_co_id")) {
//                val ucoid = NetUtils.urlDecode(cookie.value)
//                UserInfoManager.updateUCoId(ucoid)
//            }
//            //公司名称
//            if (cookieName.contains("u_co_name")) {
//                UserInfoManager.updateUCoName(NetUtils.urlDecode(cookie.value))
//            }
//            //app版本 15聚货通版本 其他采购商版本
//            if (cookieName.contains("u_apps")) {
//                val u_apps = NetUtils.urlDecode(cookie.value)
//                UserInfoManager.updateUApps(u_apps)
//            }
//            val data = model.data
//            if (data != null && data is LoginModel) {
//                //密码安全等级
//                val safeLevel = data.safeLevel ?: ""
//                UserInfoManager.updateSafeLevel(safeLevel)
//                //手机号
//                val mobile = data.mobile ?: ""
//                UserInfoManager.updateMobile(mobile)
//            }
//            //服务器环境
//            if (cookieName.contains("u_env")) {
//                UserInfoManager.updateServerEnv(cookie.value)
//            }
//            //用户角色
//            if (cookieName.contains("u_r")) {
//                UserInfoManager.updateUserRole(NetUtils.urlDecode(cookie.value))
//            }
//            //u_json {"t":"2022-8-5+11:18:19","co_type":"老A扶持班","proxy":null,"ug_id":"","dbc":"1","tt":"1","apps":"","pwd_valid":"0","ssi":null,"sign":""}
//            if (cookieName.contains("u_json")) {
//                val uJsonStr = NetUtils.urlDecode(cookie.value)
//                if (!uJsonStr.isNullOrEmpty()) {
//                    val uJsonObject = JSONObject.parseObject(uJsonStr)
//                    if (uJsonObject.contains("co_type")) {
//                        UserInfoManager.updateCoType(uJsonObject.getString("co_type"))
//                    }
//                }
//            }
//
//            //处理h5
//            CookieManager.getInstance().setCookie(SPCookieStore.COOK_HOST, cookie.toString())
//        }
//
//        if (Build.VERSION.SDK_INT < 21) {
//            CookieSyncManager.getInstance().sync()
//        } else {
//            CookieManager.getInstance().flush()
//        }
//    }

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
//                if (request != null) {
//                    NetPointEvent.addRequestBieErrorEvent(
//                        request,
//                        request.url.toString(),
//                        "错误码：${it.code}；错误信息：${it.message}"
//                    )
//                }
                if (-1 == it.code) {
                    gotoLoginActivity(it)
                } else {
                    val responseException = ResponseException(it.code, it)
                    responseException.message =
                        if (it.message?.startsWith("后端接口超时") == true
                            || it.message?.startsWith("Gateway Timeout") == true
                        ) "连接超时"
                        else it.message
                    responseException
                }
            }

            is JsonParseException,
            is JSONException,
            is ParseException -> {
//                if (request != null) {
//                    NetPointEvent.addRequestErrorEvent(
//                        request,
//                        request.url.toString(),
//                        "解析错误=${it}"
//                    )
//                }
                ResponseException(ErrorCode.PARSE_ERROR, it, "解析错误")
            }

            is ConnectException -> {
//                if (request != null) {
//                    NetPointEvent.addRequestErrorEvent(
//                        request,
//                        request.url.toString(),
//                        "连接失败=${it}"
//                    )
//                }
                ResponseException(ErrorCode.NET_ERROR, it, "连接失败")
            }

            is SSLHandshakeException,
            is SSLException -> {
//                if (request != null) {
//                    NetPointEvent.addRequestErrorEvent(
//                        request,
//                        request.url.toString(),
//                        "证书验证失败=${it}"
//                    )
//                }
                ResponseException(ErrorCode.SSL_ERROR, it, "证书验证失败")
            }

            is InterruptedIOException,
            is ConnectTimeoutException,
            is SocketTimeoutException,
            is SocketException -> {
//                if (request != null) {
//                    NetPointEvent.addRequestErrorEvent(
//                        request,
//                        request.url.toString(),
//                        "连接超时=${it}"
//                    )
//                }
                ResponseException(ErrorCode.TIMEOUT_ERROR, it, "连接超时")
            }
            /*is MalformedJsonException -> {
                gotoLoginActivity(it)
            }*/
            else -> {
//                if (request != null) {
//                    NetPointEvent.addRequestErrorEvent(
//                        request,
//                        request.url.toString(),
//                        "未知错误=${it}"
//                    )
//                }
                ResponseException(ErrorCode.UNKNOWN, it, "未知错误，请重启APP")
            }
        }
    }

    /**
     * 跳转到登录界面
     */
    private fun gotoLoginActivity(it: Throwable): ResponseException {
        //DFLoginTint.showNow()
        return ResponseException(-1, it, "用户登录信息已失效，请重新登录")
    }
}