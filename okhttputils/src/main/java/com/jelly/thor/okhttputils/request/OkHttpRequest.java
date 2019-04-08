package com.jelly.thor.okhttputils.request;

import com.jelly.thor.okhttputils.OkHttpUtils;
import com.jelly.thor.okhttputils.tag.TagBeen;
import com.jelly.thor.okhttputils.utils.Exceptions;

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
    protected String url;
    protected Object tag;
    protected Map<String, String> params;
    protected Map<String, String> headers;
    protected int id;
    public boolean isShowDialog;
    public boolean isShowToast;

    protected Request.Builder builder = new Request.Builder();

    /**
     * get调用
     */
    protected OkHttpRequest(String url, Object tag, Map<String, String> headers, int id, boolean isShowDialog, boolean isShowToast) {
        init(url, tag, null, headers, id, isShowDialog, isShowToast);
    }

    /**
     * 参数是键值对格式
     */
    protected OkHttpRequest(String url, Object tag, Map<String, String> params, Map<String, String> headers, int id, boolean isShowDialog, boolean isShowToast) {
        init(url, tag, params, headers, id, isShowDialog, isShowToast);
    }

    /**
     * 初始化
     */
    private void init(String url, Object tag, Map<String, String> params, Map<String, String> headers, int id, boolean isShowDialog, boolean isShowToast) {
        this.url = url;
        this.tag = tag;
        this.params = params;
        this.headers = headers;
        this.id = id;
        this.isShowDialog = isShowDialog;
        this.isShowToast = isShowToast;

        if (url == null) {
            Exceptions.illegalArgument("url can not be null.");
        }

        initBuilder();
    }

    /**
     * 初始化一些基本参数 url , tag , headers
     */
    private void initBuilder() {
        builder.url(url);

        TagBeen tagBeen = new TagBeen();
        if (tag != null) {
            tagBeen.setTag(tag);
        }
        tagBeen.setShowDialog(isShowDialog);
        builder.tag(tagBeen);

        appendHeaders();
    }

    /**
     * post请求添加请求体
     */
    protected abstract RequestBody requestBody();

    public RequestCall build() {
        return new RequestCall(this);
    }

    private void appendHeaders() {
        Headers.Builder headerBuilder = null;
        if (headers != null && !headers.isEmpty()) {
            headerBuilder = new Headers.Builder();
            for (String key : headers.keySet()) {
                headerBuilder.add(key, headers.get(key));
            }
        }

        Map<String, String> commonHeaders = OkHttpUtils.getInstance().getCommonHeaders();
        if (commonHeaders != null && !commonHeaders.isEmpty()) {
            if (headerBuilder == null) {
                headerBuilder = new Headers.Builder();
            }
            for (String key : commonHeaders.keySet()) {
                headerBuilder.add(key, commonHeaders.get(key));
            }
        }

        if (headerBuilder != null) {
            builder.headers(headerBuilder.build());
        }
    }

    int getId() {
        return id;
    }

    Request getRequest() {
        //post请求会有params参数
        RequestBody requestBody = requestBody();
        if (requestBody != null) {
            builder.post(requestBody);
        }
        return builder.build();
    }
}
