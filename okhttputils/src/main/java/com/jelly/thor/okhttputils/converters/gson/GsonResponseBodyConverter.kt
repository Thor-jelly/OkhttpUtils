package com.jelly.thor.okhttputils.converters.gson

import com.google.gson.Gson
import com.google.gson.internal.`$Gson$Types`
import com.google.gson.reflect.TypeToken
import com.jelly.thor.okhttputils.callback.ParseDataUtils
import com.jelly.thor.okhttputils.converters.Converter
import com.jelly.thor.okhttputils.converters.IRefParamsType
import com.jelly.thor.okhttputils.exception.ResponseException
import com.jelly.thor.okhttputils.model.ResponseModel
import com.jelly.thor.okhttputils.utils.ErrorCode
import okhttp3.Request
import okhttp3.Response
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * 创建人：吴冬冬<br/>
 * 创建时间：2023/4/23 11:49 <br/>
 */
class GsonResponseBodyConverter<T>(private val gson: Gson) : Converter {
    override fun <T> startConverter(
        id: Int,
        p: IRefParamsType<T>,
        request: Request?,
        response: Response,
        responseClazz: Class<T>?
    ): T {
        val genericSuperclass = p.javaClass.genericSuperclass
        if (genericSuperclass !is ParameterizedType) {
            val parseData = ParseDataUtils.parseData<T>(id, response, responseClazz)
            return parseData
        }
        val actualTypeArguments = genericSuperclass.actualTypeArguments
        if (actualTypeArguments.isEmpty()) {
            val parseData = ParseDataUtils.parseData<T>(id, response, responseClazz)
            return parseData
        }
        val type = actualTypeArguments[0]
        //return ParseDataUtils.parseData<T>(id, response, type)
        if (type !is ParameterizedType) {
            val parseData =
                ParseDataUtils.parseData<T>(id, response, responseClazz ?: type as Class<*>)
            return parseData
        }
        if (ResponseModel::class.java.canonicalName == (type.rawType as Class<*>).canonicalName) {
            val getPTypeImpl = `$Gson$Types`.newParameterizedTypeWithOwner(
                type.ownerType,
                type.rawType,
                *type.actualTypeArguments
            )
            val parseData = ParseDataUtils.parseData<T>(
                id,
                response,
                ResponseModel::class.java,
                getPTypeImpl
            )
            return parseData
        } else {
            val inTypeParamsImpl = `$Gson$Types`.newParameterizedTypeWithOwner(
                type.ownerType,
                type.rawType,
                *type.actualTypeArguments
            )
            val outTypeParamsImpl = `$Gson$Types`.newParameterizedTypeWithOwner(
                null,
                ResponseModel::class.java,
                *arrayOf(inTypeParamsImpl),
            )
            val parseData = ParseDataUtils.parseData<T>(id, response, null, outTypeParamsImpl)
            return parseData
        }
    }

    override fun <T> convert(
        id: Int,
        response: Response,
        responseStr: String?,
        clazz: Class<*>?,
        parameterizedTypeImpl: Type?
    ): Any {
        val model = if (parameterizedTypeImpl != null) {
            try {
                gson.fromJson<ResponseModel<T>>(responseStr, parameterizedTypeImpl)
            } catch (e: Exception) {
                //Timber.tag("123===").e(e)
                throw ResponseException(ErrorCode.PARSE_ERROR, e, "解析错误")
            }
        } else {
            try {
                gson.fromJson<ResponseModel<T>>(
                    responseStr,
                    TypeToken.getParameterized(ResponseModel::class.java, clazz).type
                )
            } catch (e: Exception) {
                //Timber.tag("123===").e("url:${response.request.url} \n $e")
                throw ResponseException(ErrorCode.PARSE_ERROR, e, "解析错误")
            }
        }
        return model
    }
}