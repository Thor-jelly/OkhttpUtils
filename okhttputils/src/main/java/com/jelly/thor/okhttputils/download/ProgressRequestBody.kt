package com.jelly.thor.okhttputils.download

import okhttp3.MediaType
import okhttp3.RequestBody
import okio.Buffer
import okio.BufferedSink
import okio.ForwardingSink
import okio.Sink
import okio.buffer

/**
 * 创建人：吴冬冬<br/>
 * 创建时间：2023/4/25 17:53 <br/>
 */
class ProgressRequestBody(
    private val body: RequestBody,
    private val listener: OkHttpUploadProgressListener
) : RequestBody() {
    override fun contentLength(): Long = body.contentLength()

    override fun contentType(): MediaType? = body.contentType()
    override fun writeTo(sink: BufferedSink) {
        val progressBuffer = sink(sink).buffer()
        body.writeTo(progressBuffer)
        // *** 注意 ***
        // progressSink 是个 buffer，走到这里 body 写完了，但是 buffer 里的不一定完全写入 sink
        // 所以要手动 flush 一下，等待数据写入完毕
        progressBuffer.flush()
    }

    private fun sink(sink: BufferedSink): Sink {
        return object : ForwardingSink(sink) {
            private var current: Long = 0
            override fun write(source: Buffer, byteCount: Long) {
                super.write(source, byteCount)
                if (byteCount <= 0) {
                    return
                }
                current += byteCount
                listener.progress(current, contentLength())
            }
        }
    }
}