package com.jelly.thor.okhttputils.request;

import com.jelly.thor.okhttputils.builder.GetBuilder;
import com.jelly.thor.okhttputils.builder.OkHttpRequestBuilder;

import java.util.Map;

import okhttp3.RequestBody;

/**
 * 类描述：get网络请求<br/>
 * 创建人：吴冬冬<br/>
 * 创建时间：2018/5/14 15:24 <br/>
 */
public class GetRequest extends OkHttpRequest {
    public GetRequest(String url, OkHttpRequestBuilder<GetBuilder> okHttpRequestBuilder) {
        super(url, okHttpRequestBuilder);
    }

    @Override
    protected RequestBody requestBody() {
        return null;
    }
}