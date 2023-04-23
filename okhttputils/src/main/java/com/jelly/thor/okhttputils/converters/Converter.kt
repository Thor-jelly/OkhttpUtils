package com.jelly.thor.okhttputils.converters

import okhttp3.Request
import okhttp3.Response

/**
 * 创建人：吴冬冬<br/>
 * 创建时间：2023/4/23 11:50 <br/>
 */
abstract class Converter<T>() {
    /**
     * @param p 必须实现IRefParamsType 并且是继承一个父类 第一个泛型是返回参数
     */
    abstract fun converter(
        id: Int = 0,
        p: IRefParamsType<T> = object : RefParamsType<T>() {},
        request: Request?,
        response: Response,
        responseClazz: Class<T>?
    ): T
}