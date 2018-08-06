package com.example.okhttputils.builder;

import java.util.Map;

/**
 * 类描述：附加参数接口<br/>
 * 创建人：吴冬冬<br/>
 * 创建时间：2018/5/14 15:06 <br/>
 */
public interface HasParamsable {
    OkHttpRequestBuilder params(Map<String, String> params);
    OkHttpRequestBuilder addParam(String key, String value);
}
