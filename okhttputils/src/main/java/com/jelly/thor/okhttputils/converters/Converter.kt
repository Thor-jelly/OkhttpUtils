package com.jelly.thor.okhttputils.converters

import com.jelly.thor.okhttputils.utils.ErrorCode
import com.jelly.thor.okhttputils.exception.ServerException
import okhttp3.Request
import okhttp3.Response
import java.lang.reflect.Type
import java.nio.charset.Charset

/**
 * 创建人：吴冬冬<br/>
 * 创建时间：2023/4/23 11:50 <br/>
 */
interface Converter {
    /**
     * @param p 必须实现IRefParamsType 并且是继承一个父类 第一个泛型是返回参数
     */
    fun <T> startConverter(
        id: Int = 0,
        p: IRefParamsType<T> = object : RefParamsType<T>() {},
        request: Request?,
        response: Response,
        responseClazz: Class<T>?
    ): T

    abstract fun <T> convert(
        id: Int,
        response: Response,
        responseStr: String?,
        clazz: Class<*>?,
        parameterizedTypeImpl: Type?
    ): Any

    abstract class Factory {
        /**
         * @param p 用来获取类型
         * @param responseClazz kotlin可以获取到 Java方法只能是null
         */
        abstract fun <T> responseBodyConverter(
            id: Int,
            p: IRefParamsType<T>,
            request: Request?,
            response: Response,
            responseClazz: Class<T>?
        ): T

        fun convertStr(
            id: Int,
            response: Response,
            clazz: Class<*>?,
            parameterizedTypeImpl: Type?
        ): String {
            //是否需要转换数据
            val isDataConversion =
                (clazz != null && response.javaClass != clazz) || parameterizedTypeImpl != null
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
            return responseStr
        }

        abstract fun <T> convert(
            id: Int,
            response: Response,
            responseStr: String?,
            clazz: Class<*>?,
            parameterizedTypeImpl: Type?
        ): Any
    }
}