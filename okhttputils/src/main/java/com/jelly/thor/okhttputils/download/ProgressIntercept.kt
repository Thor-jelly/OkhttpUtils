package com.jelly.thor.okhttputils.download

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody

/**
 * 类描述：文件进度拦截器 <br/>
 * 创建人：吴冬冬<br/>
 * 创建时间：2023/4/25 17:41 <br/>
 */
class ProgressIntercept private constructor() : Interceptor {
    private var mUploadListener: OkHttpUploadProgressListener? = null
    private var mDownloadListener: OkHttpDownloadProgressListener? = null

    constructor(uploadListener: OkHttpUploadProgressListener) : this() {
        mUploadListener = uploadListener
    }

    constructor(downloadListener: OkHttpDownloadProgressListener) : this() {
        mDownloadListener = downloadListener
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val oldRequest = chain.request()
        //替换请求body 实现上传的进度监听
        val newRequest = replaceRequestBody(oldRequest, mUploadListener)
        val oldResponse = chain.proceed(newRequest)
        //替换相应 body 实现下载的进度监听
        return replaceResponseBody(oldResponse, mDownloadListener)
    }

    /**
     * 替换请求body 实现上传的进度监听
     */
    private fun replaceRequestBody(
        oldRequest: Request,
        uploadListener: OkHttpUploadProgressListener?
    ): Request {
        if (uploadListener == null) {
            return oldRequest
        }
        val body = oldRequest.body ?: return oldRequest

        return oldRequest.newBuilder()
            .method(oldRequest.method, ProgressRequestBody(body, uploadListener))
            .build()
    }

    /**
     * 替换相应 body 实现下载的进度监听
     */
    private fun replaceResponseBody(
        oldResponse: Response,
        downloadListener: OkHttpDownloadProgressListener?
    ): Response {
        if (downloadListener == null) {
            return oldResponse
        }
        val body = oldResponse.body ?: oldResponse
        return oldResponse.newBuilder()
            .body(ProgressResponseBody(body as ResponseBody, downloadListener))
            .build()
    }
}