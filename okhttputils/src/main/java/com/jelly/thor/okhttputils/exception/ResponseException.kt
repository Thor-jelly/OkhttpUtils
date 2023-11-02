package com.jelly.thor.okhttputils.exception

/**
 * 创建人：吴冬冬<br/>
 * 创建时间：2021/3/3 17:53 <br/>
 */
data class ResponseException(
    var code: Int,
    val throwable: Throwable,
    override var message: String? = null
) : Exception(message, throwable)