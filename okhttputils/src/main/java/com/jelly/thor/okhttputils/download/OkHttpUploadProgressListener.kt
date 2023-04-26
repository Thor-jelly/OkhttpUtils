package com.jelly.thor.okhttputils.download

/**
 * 创建人：吴冬冬<br/>
 * 创建时间：2023/4/25 17:47 <br/>
 */
interface OkHttpUploadProgressListener {
    fun progress(current: Long, contentLength: Long) {}
}