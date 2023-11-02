package com.jelly.thor.okhttputils.callback

import com.jelly.thor.okhttputils.exception.ResponseException
import okhttp3.Request
import okhttp3.Response
import java.lang.reflect.Type

/**
 * 类描述：数据处理 <br/>
 * 创建人：吴冬冬<br/>
 * 创建时间：2022/12/5 14:28 <br/>
 */
interface IParseData {
//    fun <T> parseData(
//        id: Int,
//        response: Response,
//        type: Type
//    ): T

    fun <T> parseData(
        id: Int,
        response: Response,
        clazz: Class<*>? = null,
        parameterizedTypeImpl: Type? = null
    ): T

    fun handleError(it: Throwable, request: Request?): ResponseException
}