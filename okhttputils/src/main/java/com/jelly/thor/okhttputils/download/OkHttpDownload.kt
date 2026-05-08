package com.jelly.thor.okhttputils.download

import com.jelly.thor.okhttputils.OkHttpUtils
import com.jelly.thor.okhttputils.utils.CommontUtils
import com.jelly.thor.okhttputils.utils.GetApplication
import com.jelly.thor.okhttputils.utils.Platform
import com.jelly.thor.okhttputils.utils.file.FileInProgress
import com.jelly.thor.okhttputils.utils.file.save2File
import com.jelly.thor.okhttputils.utils.formatNumber
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

/**
 * 创建人：吴冬冬<br/>
 * 创建时间：2023/4/25 14:56 <br/>
 */
object OkHttpDownload {
    /**
     * @param destFileName 目标文件存储的文件名
     * @param destFileDir 目标文件存储的文件夹路径
     */
    @JvmStatic
    @JvmOverloads
    fun download(url: String, destFileName: String, downloadListener: OkHttpDownloadListener? = null) {
        this.download(url, destFileName, "ddw/", downloadListener)
    }

    /**
     * @param destFileName 目标文件存储的文件名
     * @param destFileDir 目标文件存储的文件夹路径 //"ddw/"
     */
    @JvmStatic
    @JvmOverloads
    fun download(
        url: String,
        destFileName: String,
        destFileDir: String,
        downloadListener: OkHttpDownloadListener? = null////文件下载占比例80% 保存文件占20%
    ) {
        //判断是否有网络
        val isNet = CommontUtils.networkAvailable()
        if (!isNet) {
            downloadListener?.onError("当前没有网络！")
            return
        }

        val oldOkHttpClient = OkHttpUtils.getInstance().getOkHttpClient()
        val request: Request = Request.Builder().url(url).build()
        
        //构建进度监听器
        val listener: OkHttpDownloadProgressListener = object : OkHttpDownloadProgressListener {
            override fun progress(current: Long, contentLength: Long) {
                Platform.get().execute {
                    //文件下载占比例80% 保存文件占20%
                    val progress = (current * 100.0 / (1.2 * contentLength)).toString().formatNumber(2, true)
                    downloadListener?.progress(current, contentLength, progress)
                }
            }
        }
        
        //创建OkHttpClient，并添加进度拦截器
        val newBuilder = oldOkHttpClient.newBuilder()
        newBuilder.addInterceptor(ProgressIntercept(listener))
        val client: OkHttpClient = newBuilder.build()
        
        //发送请求
        val call: Call = client.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                if (!call.isCanceled()) {
                    call.cancel()
                }
                Platform.get().execute {
                    downloadListener?.onError("网络异常！")
                }
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    val body = response.body ?: run {
                        Platform.get().execute {
                            downloadListener?.onError("响应体为空")
                        }
                        return
                    }
                    
                    val uri = body.source().save2File(
                        GetApplication.get(),
                        destFileName,
                        destFileDir,
                        object : FileInProgress(body.contentLength()) {
                            override fun inProgress(current: Long, total: Long) {
                                Platform.get().execute {
                                    //文件下载占比例80% 保存文件占20%
                                    val progress = (80 + current * 100.0 / total * 0.2).toString().formatNumber(2, true)
                                    downloadListener?.progress(current, total, progress)
                                }
                            }
                        }
                    )
                    Platform.get().execute {
                        downloadListener?.onSuccess(uri)
                    }
                } catch (e: Exception) {
                    Platform.get().execute {
                        downloadListener?.onError("保存文件异常")
                    }
                }
            }
        })
    }
}