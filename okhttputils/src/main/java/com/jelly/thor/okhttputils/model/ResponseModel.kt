package com.jelly.thor.okhttputils.model

/**
 * 类描述：网络返回数据，如果后台返回不是这个格式请自定义成自己样式，
 * 并重写okhttp callBack 跟rxjava扩展方法dataConversion <br/>
 * 创建人：吴冬冬<br/>
 * 创建时间：2022/5/5 17:29 <br/>
 */
data class ResponseModel<T>(
    val code: Int,
    val data: T?,
    val msg: String?
) {
    /**
     * 网络返回正确，其他code表示返回错误 需要从msg中获取 错误信息
     */
    fun isSuccess(): Boolean {
        return code == 0
    }
}