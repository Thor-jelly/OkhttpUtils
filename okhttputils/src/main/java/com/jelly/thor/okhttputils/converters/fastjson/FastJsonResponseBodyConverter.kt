package com.jelly.thor.okhttputils.converters.fastjson

import com.alibaba.fastjson.util.ParameterizedTypeImpl
import com.jelly.thor.okhttputils.callback.ParseDataUtils
import com.jelly.thor.okhttputils.model.ResponseModel
import com.jelly.thor.okhttputils.converters.Converter
import com.jelly.thor.okhttputils.converters.IRefParamsType
import okhttp3.Request
import okhttp3.Response
import java.lang.reflect.ParameterizedType

/**
 * 创建人：吴冬冬<br/>
 * 创建时间：2023/4/23 11:49 <br/>
 */
class FastJsonResponseBodyConverter<T> : Converter<T>() {
    override fun converter(
        id: Int,
        p: IRefParamsType<T>,
        request: Request?,
        response: Response,
        responseClazz: Class<T>?
    ): T {
        val genericSuperclass = p.javaClass.genericSuperclass
        if (genericSuperclass !is ParameterizedType) {
            val parseData =
                ParseDataUtils.parseData<T>(id, response, responseClazz)
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
                ParseDataUtils.parseData<T>(id, response, responseClazz ?: type as Class<T>)
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
                responseClazz ?: type as Class<T>,
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
}