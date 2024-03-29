package com.jelly.thor.okhttputils.converters.fastjson

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.TypeReference
import com.alibaba.fastjson.util.ParameterizedTypeImpl
import com.jelly.thor.okhttputils.callback.ParseDataUtils
import com.jelly.thor.okhttputils.converters.Converter
import com.jelly.thor.okhttputils.converters.IRefParamsType
import com.jelly.thor.okhttputils.model.ResponseModel
import com.jelly.thor.okhttputils.utils.ErrorCode
import com.jelly.thor.okhttputils.exception.ResponseException
import okhttp3.Request
import okhttp3.Response
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * 创建人：吴冬冬<br/>
 * 创建时间：2023/4/23 11:49 <br/>
 */
class FastJsonResponseBodyConverter<T> : Converter {
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
        if (type !is ParameterizedType) {
            val parseData =
                ParseDataUtils.parseData<T>(id, response, responseClazz ?: type as Class<*>)
            return parseData
        }
        if (ResponseModel::class.java.canonicalName == (type.rawType as Class<*>).canonicalName) {
            val getPTypeImpl = ParameterizedTypeImpl(
                type.actualTypeArguments,
                type.ownerType,
                type.rawType
            )
            val parseData = ParseDataUtils.parseData<T>(
                id,
                response,
                ResponseModel::class.java,
                getPTypeImpl
            )
            return parseData
        } else {
            val inTypeParamsImpl = ParameterizedTypeImpl(
                type.actualTypeArguments,
                type.ownerType,
                type.rawType
            )
            val outTypeParamsImpl = ParameterizedTypeImpl(
                arrayOf(inTypeParamsImpl),
                null,
                ResponseModel::class.java
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
                JSON.parseObject(responseStr, parameterizedTypeImpl) as ResponseModel<*>
            } catch (e: Exception) {
                throw ResponseException(ErrorCode.PARSE_ERROR, e, "解析错误")
            }
        } else {
            try {
                JSON.parseObject(
                    responseStr,
                    object : TypeReference<ResponseModel<T>>(clazz) {}) as ResponseModel<T>
            } catch (e: Exception) {
                throw ResponseException(ErrorCode.PARSE_ERROR, e, "解析错误")
            }
        }
        return model
    }
}