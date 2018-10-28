package com.jelly.thor.okhttputils.builder;

import android.webkit.URLUtil;

import com.jelly.thor.okhttputils.OkHttpUtils;
import com.jelly.thor.okhttputils.request.PostFormRequest;
import com.jelly.thor.okhttputils.request.RequestCall;

import java.util.LinkedHashMap;
import java.util.Map;

import androidx.annotation.NonNull;

/**
 * 类描述：post build <br/>
 * 创建人：吴冬冬<br/>
 * 创建时间：2018/5/14 17:54 <br/>
 */
public class PostFormBuilder extends OkHttpRequestBuilder<PostFormBuilder> implements HasParamsable, HasHeadersable {
    @Override
    public PostFormBuilder params(@NonNull Map<String, String> params) {
        if (this.params == null) {
            this.params = new LinkedHashMap<>();
        }
        this.params.putAll(params);
        return this;
    }

    @Override
    public PostFormBuilder addParam(@NonNull String key, @NonNull String value) {
        if (this.params == null) {
            params = new LinkedHashMap<>();
        }
        params.put(key, value);
        return this;
    }

    @Override
    public PostFormBuilder headers(@NonNull Map<String, String> headers) {
        if (this.headers == null) {
            this.headers = new LinkedHashMap<>();
        }
        this.headers.putAll(headers);
        return this;
    }

    @Override
    public PostFormBuilder addHeader(@NonNull String key, @NonNull String value) {
        if (this.headers == null) {
            this.headers = new LinkedHashMap<>();
        }
        this.headers.put(key, value);
        return this;
    }

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
        return new PostFormRequest(myUrl, tag, params, headers, id, isShowDialog, isShowToast).build();
    }
}
