package com.jelly.thor.okhttputils.builder;

import android.net.Uri;
import android.text.TextUtils;
import android.webkit.URLUtil;

import com.jelly.thor.okhttputils.OkHttpUtils;
import com.jelly.thor.okhttputils.request.RequestCall;

import java.util.LinkedHashMap;
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
    protected String getNewUrl() {
        if (TextUtils.isEmpty(url)) {
            return url;
        }
        // 如果url已经是完整URL，直接返回
        if (URLUtil.isValidUrl(url)) {
            return url;
        }
        // 优先使用builder设置的baseUrl
        String effectiveBaseUrl = !TextUtils.isEmpty(baseUrl) 
            ? baseUrl 
            : OkHttpUtils.getInstance().getBaseUrl();
        // 如果baseUrl为空，直接返回url
        if (TextUtils.isEmpty(effectiveBaseUrl)) {
            return url;
        }
        // 拼接baseUrl和url
        return effectiveBaseUrl + url;
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

    /**
     * 拼接url和参数（公共方法，供子类使用）
     */
    protected String appendParams(String url, Map<String, String> params) {
        if (url == null || params == null || params.isEmpty()) {
            return url;
        }
        Uri.Builder builder = Uri.parse(url).buildUpon();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            builder.appendQueryParameter(entry.getKey(), entry.getValue());
        }
        return builder.build().toString();
    }

    /**
     * 初始化params Map（供HasParameters接口实现类使用）
     */
    protected Map<String, String> initParamsIfNeeded() {
        if (this.params == null) {
            this.params = new LinkedHashMap<>();
        }
        return this.params;
    }

    /**
     * 初始化headers Map（供HasHeaders接口实现类使用）
     */
    protected Map<String, String> initHeadersIfNeeded() {
        if (this.headers == null) {
            this.headers = new LinkedHashMap<>();
        }
        return this.headers;
    }
}