package com.jelly.thor.okhttputils.utils.file

import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import okio.Source
import okio.buffer
import okio.sink
import okio.source
import java.io.BufferedInputStream
import java.io.ByteArrayInputStream
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.util.Locale

/**
 * 类描述：文件扩展 <br/>
 * 创建人：吴冬冬<br/>
 * 创建时间：2022/12/20 10:33 <br/>
 */
/**
 * 图片文件夹
 */
val PIC_DIR = Environment.DIRECTORY_PICTURES

/**
 * 下载文件夹
 */
val DOWNLOADS_DIR = Environment.DIRECTORY_DOWNLOADS

/**
 * 保存Bitmap到相册的Pictures文件夹
 *
 * https://developer.android.google.cn/training/data-storage/shared/media
 *
 * @param context 上下文
 * @param fileName 文件名。 需要携带后缀
 * @param relativePath 相对于Pictures的路径
 * @param quality 质量
 */
@JvmOverloads
fun Bitmap?.save2Pic(
    context: Context, fileName: String, relativePath: String? = "ddw/", quality: Int = 100
): Uri? {
    if (this == null) {
        return null
    }
    // 插入图片信息
    val contentResolver = context.contentResolver
    val imageUri = contentResolver.insertMediaFile(fileName, relativePath)
    if (imageUri == null) {
        return null
    }

    // 保存图片
    (imageUri.outputStream(contentResolver) ?: return null).use {
        val format = fileName.getBitmapFormat()
        this@save2Pic.compress(format, quality, it)
        imageUri.finishPending(context, contentResolver, fileName)
    }
    return imageUri
}

/**
 * 复制文件到下载
 * @param fileName 文件名。 需要携带后缀
 * @param relativePath 相对于Pictures的路径
 * @param progressCallback 文件进度回调
 */
@JvmOverloads
fun File?.save2File(
    context: Context,
    fileName: String,
    relativePath: String? = "ddw/",
    progressCallback: FileInProgress? = null
): Uri? {
    if (this == null || !this.exists() || !this.canRead()) {
        return null
    }
    return this.inputStream().save2File(context, fileName, relativePath, progressCallback)
}

/**
 * 保存文件到下载
 * @param fileName 文件名。 需要携带后缀
 * @param relativePath 相对于Pictures的路径
 * @param progressCallback 文件进度回调
 */
@JvmOverloads
fun InputStream?.save2File(
    context: Context,
    fileName: String,
    relativePath: String? = "ddw/",
    progressCallback: FileInProgress? = null
): Uri? {
    if (this == null) {
        return null
    }
    val uri = this.source().save2File(context, fileName, relativePath, progressCallback)
    return uri
}

@JvmOverloads
fun Source?.save2File(
    context: Context,
    fileName: String,
    relativePath: String? = "ddw/",
    progressCallback: FileInProgress? = null
): Uri? {
    if (this == null) {
        return null
    }
    val contentResolver = context.contentResolver
    val uri = contentResolver.insertMediaFile(fileName, relativePath)
    if (uri == null) {
        return null
    }

    val sink = uri.outputStream(contentResolver)?.sink() ?: return null
    sink.use { outSink ->
        val bufferSink = outSink.buffer()
        val buffer = bufferSink.buffer
        this.use { input ->
            var totalBytesRead = 0L
            while (true) {
                val readCount: Long = input.read(buffer, DEFAULT_BUFFER_SIZE.toLong())
                if (readCount == -1L) break
                totalBytesRead += readCount
                bufferSink.emitCompleteSegments()
                bufferSink.flush()
                progressCallback?.inProgress(
                    totalBytesRead,
                    progressCallback.totalSize
                )
            }
        }
        uri.finishPending(context, contentResolver, fileName)
    }
    return uri
}

/**
 * 通知刷新
 */
private fun Uri?.finishPending(
    context: Context, contentResolver: ContentResolver, fileName: String
) {
    if (this == null) {
        return
    }
    val contentValues = ContentValues()
    uri2File()?.let {
        contentValues.put(MediaStore.MediaColumns.SIZE, it.length())
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        // Android Q添加了IS_PENDING状态，为0时其他应用才可见
        contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
        contentResolver.update(this, contentValues, null, null)
    } else {
        contentResolver.update(this, contentValues, null, null)
        // 通知媒体库更新
        val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, this)
        context.sendBroadcast(intent)
    }
}

private fun Uri?.outputStream(contentResolver: ContentResolver): OutputStream? {
    if (this == null) {
        return null
    }
    return try {
        contentResolver.openOutputStream(this)
    } catch (e: Exception) {
        null
    }
}

/**
 * 获取bitmap 压缩格式
 */
private fun String.getBitmapFormat(): Bitmap.CompressFormat {
    val fileName = this.lowercase()
    return when {
        fileName.endsWith(".png") -> Bitmap.CompressFormat.PNG
        fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") -> Bitmap.CompressFormat.JPEG
        fileName.endsWith(".webp") -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) Bitmap.CompressFormat.WEBP_LOSSLESS
        else Bitmap.CompressFormat.WEBP

        else -> Bitmap.CompressFormat.PNG
    }
}

/**
 * @link 系统文件 MediaFile中可以查看/copy
 */
private fun String.getMineType(): String {
    val fileName = this.lowercase()
    return when {
        fileName.endsWith(".png") -> "image/png"
        fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") -> "image/jpeg"
        fileName.endsWith(".webp") -> "image/webp"
        fileName.endsWith(".gif") -> "image/gif"
        fileName.endsWith(".pdf") -> "application/pdf"
        fileName.endsWith(".xlsx") -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        fileName.endsWith(".docx") -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
        fileName.endsWith(".apk") -> "application/vnd.android.package-archive"
        else -> "file/*"
    }
}

/**
 * Gets the extension of a file name, like ".png" or ".jpg".
 * <p>
 * url : https://app-xxx-oss/xxx.gif
 *  or
 * fileName : xxx.gif
 *
 * @param fullExtension true ".png" ; false "png"
 * @return fullExtension=false, "gif";
 *         fullExtension=true,  ".gif" substring时不加1
 */
private fun getExtension(pathOrName: String?, split: Char, fullExtension: Boolean = false): String {
    if (pathOrName.isNullOrBlank()) return ""
    val dot = pathOrName.lastIndexOf(split)
    return if (dot != -1) pathOrName.substring(
        if (fullExtension) dot
        else (dot + 1)
    ).lowercase(Locale.getDefault())
    else "" // No extension.
}

/**
 * @return [√] "png" ; [×] ".png"
 */
private fun getExtension(pathOrName: String): String = getExtension(pathOrName, '.', false)

/**
 * @return [√] ".png" ; [×] "png"
 */
private fun getExtensionFull(pathOrName: String): String = getExtension(pathOrName, '.', true)

/**
 * 插入文件到媒体库中
 */
private fun ContentResolver.insertMediaFile(
    fileName: String, relativePath: String?
): Uri? {
    val extensionFull = getExtensionFull(fileName)
    val newTimeFileName =
        fileName.replace(extensionFull, "${System.currentTimeMillis()}${extensionFull}")
    //图片信息
    val mineType = fileName.getMineType()
    //是否是图片格式
    val isImage = mineType.startsWith("image")
    val contentResolver = ContentValues().apply {
        put(MediaStore.MediaColumns.MIME_TYPE, mineType)
        val time = System.currentTimeMillis() / 1000
        put(MediaStore.MediaColumns.DATE_ADDED, time)
        put(MediaStore.MediaColumns.DATE_MODIFIED, time)
    }
    //保存位置
    val collection: Uri
    val dirStr = if (isImage) {
        PIC_DIR
    } else {
        DOWNLOADS_DIR
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val path = if (relativePath.isNullOrEmpty()) {
            dirStr
        } else {
            "${dirStr}/${relativePath}"
        }
        contentResolver.apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, newTimeFileName)
            put(MediaStore.MediaColumns.RELATIVE_PATH, path)
            put(MediaStore.MediaColumns.IS_PENDING, 1)
        }
        if (isImage) {
            collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            collection = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        }
        // 高版本不用查重直接插入，会自动重命名
    } else {
        val dir = Environment.getExternalStoragePublicDirectory(dirStr)
        val saveDir =
            if (relativePath.isNullOrEmpty()) dir else File(dir, relativePath)
        if (!saveDir.exists() && !saveDir.mkdirs()) {
            return null
        }
        //查重文件，如果重复文件名后添加数字
        var file = File(saveDir, newTimeFileName)
        val fileNameWithoutExtension = file.nameWithoutExtension
        val fileExtension = file.extension

        var queryUri = this.queryMediaFile28(isImage, file.absolutePath)
        var suffix = 1
        while (queryUri != null) {
            val newFileName = fileNameWithoutExtension + "(${suffix++}).${fileExtension}"
            file = File(saveDir, newFileName)
            queryUri = this.queryMediaFile28(isImage, file.absolutePath)
        }
        contentResolver.apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, file.name)
            put(MediaStore.MediaColumns.DATA, file.absolutePath)
        }

        if (isImage) {
            collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        } else {
            collection = MediaStore.Files.getContentUri("external")
        }
    }

    //插入文件信息
    return this.insert(collection, contentResolver)
}

/**
 * Android Q以下版本，查询媒体库中当前路径是否存在
 * @return 返回null表示不存在
 */
private fun ContentResolver.queryMediaFile28(isImage: Boolean, path: String): Uri? {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) return null

    val file = File(path)
    if (file.canRead() && file.exists()) {
        return Uri.fromFile(file)
    }
    //保存的位置
    val collection = if (isImage) MediaStore.Images.Media.EXTERNAL_CONTENT_URI
    else MediaStore.Files.getContentUri("external")
    val projection = arrayOf(MediaStore.MediaColumns._ID, MediaStore.MediaColumns.DATA)
    val selection = "${MediaStore.MediaColumns.DATA} == ?"
    //查询是否已经存在相同的文件
    val query = this.query(
        collection, projection, selection, arrayOf(path), null
    )
    query?.use {
        while (it.moveToNext()) {
            val idColumn = it.getColumnIndexOrThrow(MediaStore.MediaColumns._ID)
            val id = it.getLong(idColumn)
            val existsUri = ContentUris.withAppendedId(collection, id)
            return existsUri
        }
    }
    return null
}

/**
 * 安装apk文件
 */
fun Context?.installApk(uri: Uri?) {
    if (this == null || uri == null) {
        return
    }
    val intent = Intent(Intent.ACTION_VIEW)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        //Log.w(TAG, "版本大于 N ，开始使用 fileProvider 进行安装");
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        //Uri contentUri = FileProvider.getUriForFile(
        //        context
        //        , SmallApp.getAppContext().getPackageName() + ".fileProvider"
        //        , apkFile);
        intent.setDataAndType(uri, "application/vnd.android.package-archive")
    } else {
        intent.setDataAndType(
            Uri.fromFile(uri.uri2File()),
            "application/vnd.android.package-archive"
        )
    }
    startActivity(intent)
}

@JvmOverloads
fun String?.base64ToUri(
    context: Context,
    fileName: String,
    relativePath: String? = "ddw/",
): Uri? {
    if (this.isNullOrEmpty()) {
        return null
    }
    val bytes: ByteArray = Base64.decode(this, Base64.DEFAULT)
    val byteArrayInputStream = ByteArrayInputStream(bytes)
    val bufferedInputStream = BufferedInputStream(byteArrayInputStream)
    return bufferedInputStream.save2File(
        context,
        fileName,
        relativePath
    )
}