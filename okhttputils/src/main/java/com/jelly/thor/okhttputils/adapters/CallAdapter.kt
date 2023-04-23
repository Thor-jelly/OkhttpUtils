package com.jelly.thor.okhttputils.adapters


/**
 * 创建人：吴冬冬<br/>
 * 创建时间：2023/4/23 11:17 <br/>
 */
interface CallAdapter {
    abstract class Factory {
        abstract fun get(): CallAdapter
    }
}