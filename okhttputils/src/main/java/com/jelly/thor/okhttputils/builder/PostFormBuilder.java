package com.jelly.thor.okhttputils.builder;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.jelly.thor.okhttputils.request.PostFormRequest;
import com.jelly.thor.okhttputils.request.RequestCall;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * 类描述：post build <br/>
 * 创建人：吴冬冬<br/>
 * 创建时间：2018/5/14 17:54 <br/>
 */
public class PostFormBuilder extends OkHttpRequestBuilder<PostFormBuilder> implements HasParameters<PostFormBuilder>, HasHeaders<PostFormBuilder> {
    /**
     * get参数
     */
    protected Map<String, String> queryParams;

    public PostFormBuilder queryParams(@NonNull Map<String, String> params) {
        if (this.queryParams == null) {
            this.queryParams = new LinkedHashMap<>();
        }
        this.queryParams.putAll(params);
        return this;
    }

    public PostFormBuilder addQueryParam(@NonNull String key, @NonNull String value) {
        if (this.queryParams == null) {
            queryParams = new LinkedHashMap<>();
        }
        queryParams.put(key, value);
        return this;
    }

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
        String myUrl = getNewUrl();
        if (queryParams != null && !queryParams.isEmpty()) {
            myUrl = appendParams(myUrl, queryParams);
        }
        return new PostFormRequest(myUrl, this).build();
    }

    /**
     * 拼接url和参数
     */
    private String appendParams(String url, Map<String, String> params) {
        if (null == url || null == params || params.isEmpty()) {
            return url;
        }
        Uri.Builder builder = Uri.parse(url).buildUpon();
        Set<String> keys = params.keySet();
        for (String key : keys) {
            builder.appendQueryParameter(key, params.get(key));
        }
        return builder.build().toString();
    }
}