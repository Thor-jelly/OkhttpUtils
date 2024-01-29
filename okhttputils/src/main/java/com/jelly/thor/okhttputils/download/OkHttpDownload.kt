package com.jelly.thor.okhttputils.download

import com.jelly.thor.okhttputils.BuildConfig
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
        url: String, destFileName: String, destFileDir: String, downloadListener: OkHttpDownloadListener? = null//文件下载占比例80% 保存文件占20%
    ) {
        //Timber.tag("123===").d(Thread.currentThread().toString() + "===" + (Thread.currentThread() == Looper.getMainLooper().thread))

        //判断是否有网络
        val isNet = CommontUtils.networkAvailable()
        if (!isNet) {
            val errorStr = "当前没有网络！"
            //NetPointEvent.addRequestErrorEvent(url, errorStr)
            downloadListener?.onError(errorStr)
            return
        }

        val oldOkHttpClient = OkHttpUtils.getInstance().okHttpClient
        //构建一个请求
        val request: Request = Request.Builder().url(url).build()
        //构建我们的进度监听器
        val listener: OkHttpDownloadProgressListener = object : OkHttpDownloadProgressListener {
            override fun progress(current: Long, contentLength: Long) {
                //计算百分比并更新ProgressBar
                //Timber.tag("123===").d(Thread.currentThread().toString() + "===" + (Thread.currentThread() == Looper.getMainLooper().thread))
                //Timber.tag("123===").d("下载进度：" + 100 * current / contentLength + "%" + current)
                Platform.get().execute {
                    //文件下载占比例80% 保存文件占20%
                    downloadListener?.progress(current, contentLength, (current * 100.0 / (1.2 * contentLength)).toString().formatNumber(2, true))
                }
            }
        }
        //val loggingInterceptor = HttpLoggingInterceptor { msg: String? -> Timber.tag("OkHttp").d(msg) }
        //loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        //创建一个OkHttpClient，并添加网络拦截器
        val newBuilder = oldOkHttpClient.newBuilder()
        if (BuildConfig.DEBUG) {
            newBuilder.addInterceptor(ProgressIntercept(listener))
        }
        //.addInterceptor(loggingInterceptor)
        val client: OkHttpClient = newBuilder.build()
        //发送响应
        val call: Call = client.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                //在子线程中
                val failureStr1 = "OkHttp3--->>>onFailure: $e"
                //Timber.tag("OkHttpUtils").d(failureStr1)
                val failureStr2 = "OkHttp3--->>>onFailure: 当前网络是否被被取消=" + call.isCanceled()
                //Timber.tag("OkHttpUtils").d(failureStr2)
                //NetPointEvent.addRequestErrorEvent(call.request(), url, "$failureStr2 \n $failureStr1")
                if (!call.isCanceled()) {
                    call.cancel()
                }
                Platform.get().execute {
                    downloadListener?.onError("网络异常！")
                }
            }

            override fun onResponse(call: Call, response: Response) {
                //从响应体读取字节流
                try {
                    val body = response.body!!
                    //Timber.tag("123===").d("文件保存进度：==========================${body.contentLength()}")
//                    body.byteStream().save2File(
//                        BaseApplication.getAppContext(),
//                        destFileName,
//                        destFileDir,
//                        object : FileInProgress(body.contentLength()) {
//                            override fun inProgress(current: Long, total: Long) {
//                                Timber.tag("123===")
//                                    .d("文件保存进度：" + current + "==" + total)
//                            }
//                        })
                    val uri = body.source().save2File(GetApplication.get(), destFileName, destFileDir, object : FileInProgress(body.contentLength()) {
                        override fun inProgress(current: Long, total: Long) {
                            //Timber.tag("123===").d("文件保存进度：$current==$total")
                            Platform.get().execute {
                                if (BuildConfig.DEBUG) {
                                    //文件下载占比例80% 保存文件占20%
                                    downloadListener?.progress(current, total, (80 + current * 100.0 / total * 0.2).toString().formatNumber(2, true))
                                } else {
                                    downloadListener?.progress(current, total, (current * 100.0 / total).toString().formatNumber(2, true))
                                }
                            }
                        }
                    })
                    Platform.get().execute {
                        downloadListener?.onSuccess(uri)
                    }
                    //Timber.tag("123===").d("下载完成")
                } catch (e: Exception) {
                    val errorMsg = "保存文件异常"
                    //Timber.tag("123===").d(errorMsg)
                    //NetPointEvent.addRequestErrorEvent(request, url, "$errorMsg====$e")
                    Platform.get().execute {
                        downloadListener?.onError(errorMsg)
                    }
                }
            }
        })
    }
}