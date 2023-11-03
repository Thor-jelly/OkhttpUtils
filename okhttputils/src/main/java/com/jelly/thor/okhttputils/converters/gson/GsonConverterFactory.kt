package com.jelly.thor.okhttputils.converters.gson

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.ToNumberPolicy
import com.jelly.thor.okhttputils.converters.Converter
import com.jelly.thor.okhttputils.converters.IRefParamsType
import okhttp3.Request
import okhttp3.Response
import java.lang.reflect.Type

/**
 * 创建人：吴冬冬<br/>
 * 创建时间：2023/10/7 17:21 <br/>
 */
class GsonConverterFactory(private val gson: Gson) : Converter.Factory() {
    companion object {
        @JvmStatic
        fun create(): GsonConverterFactory {
            val gson = GsonBuilder().setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE).create()
            return GsonConverterFactory(gson);
        }

        @JvmStatic
        fun create(gson: Gson): GsonConverterFactory {
            return GsonConverterFactory(gson);
        }
    }

    private lateinit var converter: GsonResponseBodyConverter<*>

    override fun <T> responseBodyConverter(
        id: Int,
        p: IRefParamsType<T>,
        request: Request?,
        response: Response,
        responseClazz: Class<T>?
    ): T {
        converter = GsonResponseBodyConverter<T>(gson)
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