package com.jelly.thor.okhttputils.callback

import com.jelly.thor.okhttputils.OkHttpUtils
import com.jushuitan.jht.basemodule.utils.net.exception.ResponseException
import okhttp3.Request
import okhttp3.Response
import java.lang.reflect.ParameterizedType


/**
 * 类描述：解析数据<br/>
 * 创建人：吴冬冬<br/>
 * 创建时间：2022/4/27 11:31 <br/>
 */
object ParseDataUtils {
//    @JvmStatic
//    fun <T> parseData(
//        id: Int,
//        response: Response,
//        type: Type
//    ): T {
//        return OkHttpUtils.getInstance().parseData.parseData(
//            id,
//            response,
//            type
//        )
//    }

    @JvmOverloads
    @JvmStatic
    fun <T> parseData(
        id: Int,
        response: Response,
        clazz: Class<T>? = null,
        parameterizedTypeImpl: ParameterizedType? = null
    ): T {
        return OkHttpUtils.getInstance().parseData.parseData(
            id,
            response,
            clazz,
            parameterizedTypeImpl
        )
    }

    @JvmStatic
    fun handleError(it: Throwable, request: Request?): ResponseException {
        return OkHttpUtils.getInstance().parseData.handleError(it, request)
    }
}