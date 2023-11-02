package com.jelly.thor.okhttputils.request;

import com.jelly.thor.okhttputils.OkHttpUtils;
import com.jelly.thor.okhttputils.builder.OkHttpRequestBuilder;
import com.jelly.thor.okhttputils.tag.TagModel;

import java.util.Map;

import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * 类描述：网络请求request 组合<br/>
 * 创建人：吴冬冬<br/>
 * 创建时间：2018/5/14 15:12 <br/>
 */
public abstract class OkHttpRequest {
    public OkHttpRequestBuilder<? extends OkHttpRequestBuilder<?>> okHttpRequestBuilder;
    protected String url;

    protected Request.Builder builder = new Request.Builder();

    /**
     * GET调用
     */
    public OkHttpRequest(String url, OkHttpRequestBuilder<? extends OkHttpRequestBuilder<?>> builder) {
        okHttpRequestBuilder = builder;
        init(url);
    }

    private void init(String url) {
        this.url = url;
        if (url == null) {
            throw new IllegalArgumentException("url can not be null！");
        }

        initBuilder();
    }

    /**
     * 初始化请求的一些基本参数 url , tag , headers
     */
    private void initBuilder() {
        builder.url(url);
        TagModel tagModel = new TagModel();
        if (null != okHttpRequestBuilder.getTag()) {
            tagModel.setTag(okHttpRequestBuilder.getTag());
        }
        builder.tag(tagModel);

        appendHeaders();
    }

    /**
     * 设置初始请求头
     */
    private void appendHeaders() {
        Headers.Builder headerBuilder = null;
        //通用请求头
        Map<String, String> commonHeaders = OkHttpUtils.getInstance().getCommonHeaders();
        if (commonHeaders != null && !commonHeaders.isEmpty()) {
            headerBuilder = new Headers.Builder();

            for (Map.Entry<String, String> entry : commonHeaders.entrySet()) {
                builder.removeHeader(entry.getKey());
                headerBuilder.add(entry.getKey(), entry.getValue());
            }
        }

        //当前请求设置的请求头
        Map<String, String> headers = okHttpRequestBuilder.getHeaders();
        if (headers != null && !headers.isEmpty()) {
            if (headerBuilder == null) {
                headerBuilder = new Headers.Builder();
            }
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                builder.removeHeader(entry.getKey());
                headerBuilder.add(entry.getKey(), entry.getValue());
            }
        }

        if (headerBuilder != null) {
            builder.headers(headerBuilder.build());
        }
    }

    public RequestCall build() {
        return new RequestCall(this);
    }

    /**
     * post请求添加请求体
     */
    protected abstract RequestBody requestBody();

    Request getRequest() {
        //post请求会有params参数
        RequestBody requestBody = requestBody();
        if (requestBody != null) {
            builder.post(requestBody);
        }
        return builder.build();
    }
}