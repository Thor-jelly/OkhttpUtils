package com.example.okhttputils.builder;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.webkit.URLUtil;

import com.example.okhttputils.OkHttpUtils;
import com.example.okhttputils.request.GetRequest;
import com.example.okhttputils.request.RequestCall;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * 类描述：getBuilder <br/>
 * 创建人：吴冬冬<br/>
 * 创建时间：2018/5/14 15:05 <br/>
 */
public class GetBuilder extends OkHttpRequestBuilder<GetBuilder> implements HasParamsable, HasHeadersable {
    @Override
    public RequestCall build() {
        String myUrl;
        if (baseUrl != null) {
            if (URLUtil.isValidUrl(url)) {
                myUrl = url;
            } else {
                myUrl = baseUrl + url;
            }
        } else if (OkHttpUtils.getInstance().getBaseUrl() != null) {
            if (URLUtil.isValidUrl(url)) {
                myUrl = url;
            } else {
                myUrl = OkHttpUtils.getInstance().getBaseUrl() + url;
            }
        } else {
            myUrl = url;
        }
        Map<String, String> commonParams = OkHttpUtils.getInstance().getCommonParams();
        if (params != null) {
            if (commonParams != null && !commonParams.isEmpty()) {
                params.putAll(commonParams);
            }
            myUrl = appendParams(myUrl, params);
        } else {
            if (commonParams != null && !commonParams.isEmpty()) {
                myUrl = appendParams(myUrl, commonParams);
            }
        }
        return new GetRequest(myUrl, tag, headers, id, isShowDialog, isShowToast).build();
    }

    /**
     * 拼接url和参数
     */
    private String appendParams(String url, Map<String, String> params) {
        if (url == null || params == null || params.isEmpty()) {
            return url;
        }
        Uri.Builder builder = Uri.parse(url).buildUpon();
        Set<String> keys = params.keySet();
        for (String key : keys) {
            builder.appendQueryParameter(key, params.get(key));
        }
        return builder.build().toString();
    }


    @Override
    public GetBuilder params(@NonNull Map<String, String> params) {
        this.params = params;
        return this;
    }

    @Override
    public GetBuilder addParam(String key, String value) {
        if (this.params == null) {
            params = new LinkedHashMap<>();
        }
        params.put(key, value);
        return this;
    }

    @Override
    public GetBuilder headers(@NonNull Map<String, String> headers) {
        this.headers = headers;
        return this;
    }

    @Override
    public GetBuilder addHeader(String key, String value) {
        if (this.headers == null) {
            this.headers = new LinkedHashMap<>();
        }
        this.headers.put(key, value);
        return this;
    }
}
