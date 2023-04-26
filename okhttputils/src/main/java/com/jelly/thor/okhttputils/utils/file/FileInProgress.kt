package com.jelly.thor.okhttputils.utils.file

/**
 * 类描述：文件进度类  <br/>
 * 创建人：吴冬冬<br/>
 * 创建时间：2023/1/13 11:18 <br/>
 */
abstract class FileInProgress constructor(val totalSize: Long) {
    /**
     * @param progress 进度
     * @param total 总大小
     */
    abstract fun inProgress(progress: Long, total: Long)
}