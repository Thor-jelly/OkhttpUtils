package com.jushuitan.jht.basemodule.utils.net.exception

/**
 * 创建人：吴冬冬<br/>
 * 创建时间：2021/3/3 17:53 <br/>
 */
data class ServerException(
    var code: Int,
    override var message: String? = null
) : Exception(message)