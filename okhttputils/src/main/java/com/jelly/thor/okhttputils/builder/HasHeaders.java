package com.jelly.thor.okhttputils.builder;

import androidx.annotation.NonNull;

import java.util.Map;

/**
 * 类描述：公共请求头 <br/>
 * 创建人：吴冬冬<br/>
 * 创建时间：2018/8/6 14:18 <br/>
 */
interface HasHeaders<T extends OkHttpRequestBuilder<T>> {
    OkHttpRequestBuilder<T> headers(@NonNull Map<String, String> headers);

    OkHttpRequestBuilder<T> addHeader(@NonNull String key, @NonNull String value);
}