package com.example.okhttputils.builder;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 类描述：公共请求头 <br/>
 * 创建人：吴冬冬<br/>
 * 创建时间：2018/8/6 14:18 <br/>
 */
public interface HasHeadersable {
    OkHttpRequestBuilder headers(Map<String, String> headers);
    OkHttpRequestBuilder addHeader(String key, String value);
}
