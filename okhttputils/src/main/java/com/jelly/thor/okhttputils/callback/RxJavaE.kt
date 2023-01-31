package com.jelly.thor.okhttputils.callback

import android.net.Uri
import com.jelly.thor.okhttputils.model.ResponseModel
import com.jelly.thor.okhttputils.utils.ErrorCode
import com.jelly.thor.okhttputils.utils.GetApplication
import com.jelly.thor.okhttputils.utils.GsonTypes
import com.jelly.thor.okhttputils.utils.file.save2File
import com.jushuitan.jht.basemodule.utils.net.exception.ServerException
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.Request
import okhttp3.Response
import java.lang.reflect.ParameterizedType


/**
 * 类描述：rxjava 数据转换 <br/>
 * 创建人：吴冬冬<br/>
 * 创建时间：2022/4/27 11:28 <br/>
 */
/**
 * @param p 反射获取泛型用，不需要赋值进去
 */
inline fun <reified T : Any> Maybe<Response>.dataConversion(
    id: Int = 0,
    p: RefParamsType<T> = object : RefParamsType<T>() {}
): Maybe<T> {
    var request: Request? = null
    return this.map {
        request = it.request
        val genericSuperclass = p.javaClass.genericSuperclass
        if (genericSuperclass !is ParameterizedType) {
            return@map ParseDataUtils.parseData<T>(id, it, T::class.java)
        }
        val actualTypeArguments = genericSuperclass.actualTypeArguments
        if (actualTypeArguments.isEmpty()) {
            return@map ParseDataUtils.parseData<T>(id, it, T::class.java)
        }
        val type = actualTypeArguments[0]
        if (type !is ParameterizedType) {
            return@map ParseDataUtils.parseData<T>(id, it, T::class.java)
        }
        if (ResponseModel::class.java.canonicalName == (type.rawType as Class<*>).canonicalName) {
            val getPTypeImpl = GsonTypes.ParameterizedTypeImpl(
                type.ownerType,
                type.rawType,
                *type.actualTypeArguments
            )
            return@map ParseDataUtils.parseData(id, it, T::class.java, getPTypeImpl)
        } else {
            val inTypeParamsImpl = GsonTypes.ParameterizedTypeImpl(
                type.ownerType,
                type.rawType,
                *type.actualTypeArguments
            )
            val outTypeParamsImpl = GsonTypes.ParameterizedTypeImpl(
                null,
                ResponseModel::class.java,
                *arrayOf(inTypeParamsImpl)
            )
            val parseData = ParseDataUtils.parseData<T>(id, it, null, outTypeParamsImpl)
            return@map parseData
        }
    }.onErrorResumeNext {
        val error = ParseDataUtils.handleError(it, request)
        Maybe.error<T>(error)
    }
}

/**
 * 反射获取泛型使用
 */
abstract class RefParamsType<T>


///////////////文件下载转换//////////////////
/**
 * 不带下载进度，如果需要下载进度请使用callback方法
 *
 * @param destFileDir 目标文件存储的文件夹路径
 * @param destFileName 目标文件存储的文件名
 */
fun Maybe<Response>.fileConversion(
    destFileName: String,
    destFileDir: String = "jht/",
    savaErrorHint: String = "文件下载失败，请重试"
): Maybe<Uri> {
    var request: Request? = null
    return this.observeOn(Schedulers.io())
        .map {
            request = it.request
            try {
                val body = it.body ?: throw ServerException(ErrorCode.SAVE_FILE, savaErrorHint)
                val inputStream = body.byteStream()
                val total = body.contentLength()
                val uri = inputStream.save2File(
                    GetApplication.get(),
                    destFileName,
                    destFileDir
                ) ?: throw ServerException(ErrorCode.SAVE_FILE, savaErrorHint)
                uri
            } finally {
                try {
                    it.body!!.close()
                } catch (e: Exception) {
                }
            }
        }.onErrorResumeNext {
            val error = ParseDataUtils.handleError(it, request)
            Maybe.error(error)
        }
}