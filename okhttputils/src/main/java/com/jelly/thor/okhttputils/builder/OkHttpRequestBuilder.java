package com.jelly.thor.okhttputils.builder;

import com.jelly.thor.okhttputils.request.RequestCall;

import java.util.Map;

/**
 * 类描述：网络请求主要参数 <br/>
 * 创建人：吴冬冬<br/>
 * 创建时间：2018/5/14 15:07 <br/>
 */
public abstract class OkHttpRequestBuilder<T extends OkHttpRequestBuilder> {
    /**
     * 如果url前半段url都相同可以设置baseURL来简化url设置
     */
    protected String baseUrl;
    /**
     * url
     */
    protected String url;
    /**
     * 网络请求标志
     */
    protected Object tag;
    /**
     * 头
     */
    protected Map<String, String> headers;
    /**
     * 参数
     */
    protected Map<String, String> params;

    protected int id;

    /**
     * 是否显示弹窗
     */
    protected boolean isShowDialog = false;
    /**
     * 是否显示Toast
     */
    protected boolean isShowToast = false;

    public T isShowToast(boolean isShowToast) {
        this.isShowToast = isShowToast;
        return (T) this;
    }

    public T isShowDialog(boolean isShowDialog) {
        this.isShowDialog = isShowDialog;
        return (T) this;
    }

    public T id(int id) {
        this.id = id;
        return (T) this;
    }

    public T baseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        return (T) this;
    }

    public T url(String url) {
        this.url = url;
        return (T) this;
    }


    public T tag(Object tag) {
        this.tag = tag;
        return (T) this;
    }

    public abstract RequestCall build();
}
