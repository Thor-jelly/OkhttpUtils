package com.jelly.thor.okhttputils.builder;

import android.text.TextUtils;
import android.webkit.URLUtil;

import com.jelly.thor.okhttputils.OkHttpUtils;
import com.jelly.thor.okhttputils.request.RequestCall;

import java.util.Map;

/**
 * 类描述：网络请求主要参数 <br/>
 * 创建人：吴冬冬<br/>
 * 创建时间：2018/5/14 15:07 <br/>
 */
public abstract class OkHttpRequestBuilder<T extends OkHttpRequestBuilder<T>> {
    /**
     * 如果url前半段url都相同可以设置baseURL来简化url设置
     */
    protected String baseUrl;
    /**
     * url
     */
    protected String url;
    /**
     * 网络请求标志，用来取消网络
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

    /**
     * 用来请求多个同一网络请求 可以在返回中区分网络请求
     */
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

    /**
     * 网络请求域名拼接生成新的url
     */
    protected String getNewUrl(){
        String newUrl;
        if (!TextUtils.isEmpty(baseUrl)) {
            if (URLUtil.isValidUrl(url)) {
                newUrl = url;
            } else {
                newUrl = baseUrl + url;
            }
        } else if (!TextUtils.isEmpty(OkHttpUtils.getInstance().getBaseUrl())) {
            if (URLUtil.isValidUrl(url)) {
                newUrl = url;
            } else {
                newUrl = OkHttpUtils.getInstance().getBaseUrl() + url;
            }
        } else {
            newUrl = url;
        }
        return newUrl;
    }

    ///////////////////////GET/////////////////////////////////
    public String getBaseUrl() {
        return baseUrl;
    }

    public String getUrl() {
        return url;
    }

    public Object getTag() {
        return tag;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public int getId() {
        return id;
    }

    public boolean getIsShowDialog() {
        return isShowDialog;
    }

    public boolean getIsShowToast() {
        return isShowToast;
    }
}