package com.jushuitan.jht.basemodule.utils.kotlin

import android.content.ContentResolver
import android.net.Uri
import android.provider.MediaStore
import com.jelly.thor.okhttputils.utils.GetApplication
import java.io.File

/**
 * 类描述：file uri转换 <br/>
 * 改动自：https://github.com/Blankj/AndroidUtilCode/blob/master/lib/utilcode/src/main/java/com/blankj/utilcode/util/UriUtils.java
 * 改动自：https://github.com/javakam/FileOperator/blob/master/library_core/src/main/java/ando/file/core/FileUri.kt
 * 创建人：吴冬冬<br/>
 * 创建时间：2022/12/21 17:55 <br/>
 */
/**
 * uri转file路径
 */
fun Uri?.uri2File(): File? {
    if (this == null || this.toString().isEmpty()) return null

    if (this.scheme == ContentResolver.SCHEME_FILE && !this.path.isNullOrEmpty()) {
        return File(this.path!!)
    }

    val contentResolver = GetApplication.get().contentResolver
    val cursor = contentResolver.query(
        this,
        null,
        null,
        null,
        null
    )
    cursor?.use {
        if (cursor.moveToFirst()) {
            val index = cursor.getColumnIndex(MediaStore.MediaColumns.DATA)
            if (index == -1) {
                //Timber.tag("123===").w("uri转file失败：未获取到index")
                return null
            }
            val path = cursor.getString(index)
            //Timber.tag("123===").w("uri转file成功path=：${path}")
            return File(path)
        }
    }
    //Timber.tag("123===").w("uri转file失败：cursor 解析失败")
    return null
}
