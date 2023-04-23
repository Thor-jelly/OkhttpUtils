package com.jelly.thor.okhttputils.adapters.rxjava3

import com.jelly.thor.okhttputils.adapters.CallAdapter

/**
 * 创建人：吴冬冬<br/>
 * 创建时间：2023/4/23 11:12 <br/>
 */
class RxJava3CallAdapterFactory private constructor() : CallAdapter.Factory() {
    companion object {
        fun create(): RxJava3CallAdapterFactory {
            return RxJava3CallAdapterFactory()
        }
    }

    override fun get(): CallAdapter {
        return RxJava3CallAdapter()
    }
}