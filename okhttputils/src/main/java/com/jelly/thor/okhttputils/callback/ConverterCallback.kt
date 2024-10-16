package com.jelly.thor.okhttputils.callback

import okhttp3.Response

/**
 * 创建人：吴冬冬<br/>
 * 创建时间：2024/10/15 16:02 <br/>
 */
internal abstract class ConverterCallback {
    abstract fun onError(e: Exception)

    abstract fun onSuccess(response: Response)
}