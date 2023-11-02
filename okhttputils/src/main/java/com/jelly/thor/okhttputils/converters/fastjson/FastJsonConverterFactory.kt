package com.jelly.thor.okhttputils.converters.fastjson

import com.jelly.thor.okhttputils.converters.Converter
import com.jelly.thor.okhttputils.converters.IRefParamsType
import okhttp3.Request
import okhttp3.Response
import java.lang.reflect.Type

/**
 * 创建人：吴冬冬<br/>
 * 创建时间：2023/10/7 17:21 <br/>
 */
class FastJsonConverterFactory : Converter.Factory() {
    companion object {
        @JvmStatic
        fun create(): FastJsonConverterFactory {
            return FastJsonConverterFactory();
        }
    }

    private lateinit var converter: FastJsonResponseBodyConverter<*>

    override fun <T> responseBodyConverter(
        id: Int,
        p: IRefParamsType<T>,
        request: Request?,
        response: Response,
        responseClazz: Class<T>?
    ): T {
        converter = FastJsonResponseBodyConverter<T>()
        return converter.startConverter(id, p, response.request, response, responseClazz)
    }

    override fun <T> convert(
        id: Int,
        response: Response,
        responseStr: String?,
        clazz: Class<*>?,
        parameterizedTypeImpl: Type?
    ): Any {
        return converter.convert<T>(id, response, responseStr, clazz, parameterizedTypeImpl)
    }
}