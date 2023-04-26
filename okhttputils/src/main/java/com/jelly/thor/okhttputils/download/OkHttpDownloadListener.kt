package com.jelly.thor.okhttputils.download

import android.net.Uri
import androidx.annotation.UiThread

/**
 * 创建人：吴冬冬<br/>
 * 创建时间：2023/4/25 17:45 <br/>
 */
interface OkHttpDownloadListener {
    @UiThread
    fun onSuccess(uri: Uri?) {
    }

    @UiThread
    fun onError(errorStr: String) {
    }

    @UiThread
    fun progress(current: Long, contentLength: Long, progress: String) {
    }
}