package com.jelly.thor.okhttputils.download

/**
 * 创建人：吴冬冬<br/>
 * 创建时间：2023/4/25 17:45 <br/>
 */
interface OkHttpDownloadProgressListener {
    fun progress(current: Long, contentLength: Long) {}
}