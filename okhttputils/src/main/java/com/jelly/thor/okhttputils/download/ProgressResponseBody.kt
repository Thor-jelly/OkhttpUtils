package com.jelly.thor.okhttputils.download

import okhttp3.MediaType
import okhttp3.ResponseBody
import okio.Buffer
import okio.BufferedSource
import okio.ForwardingSource
import okio.Source
import okio.buffer

/**
 * 创建人：吴冬冬<br/>
 * 创建时间：2023/4/25 18:13 <br/>
 */
class ProgressResponseBody(
    private val body: ResponseBody,
    private val listener: OkHttpDownloadProgressListener
) : ResponseBody() {
    private val progressSource by lazy {
        source(body.source()).buffer()
    }

    override fun contentLength(): Long = body.contentLength()

    override fun contentType(): MediaType? = body.contentType()

    override fun source(): BufferedSource = progressSource

    private fun source(source: BufferedSource): Source {
        return object : ForwardingSource(source) {
            private var current: Long = 0
            override fun read(sink: Buffer, byteCount: Long): Long {
                // 没有读到末尾时更新读取长度并回调
                val bytesRead = super.read(sink, byteCount)
                if (bytesRead <= 0) {
                    return bytesRead
                }
                current += bytesRead
                listener.progress(current, contentLength())
                return bytesRead
            }
        }
    }
}