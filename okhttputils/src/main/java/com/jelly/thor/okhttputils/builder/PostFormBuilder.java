package com.jelly.thor.okhttputils.builder;

import androidx.annotation.NonNull;

import com.jelly.thor.okhttputils.request.PostFormRequest;
import com.jelly.thor.okhttputils.request.RequestCall;

import java.util.LinkedHashMap;
import java.util.Map;

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
        initParamsIfNeeded().putAll(params);
        return this;
    }

    @Override
    public PostFormBuilder addParam(@NonNull String key, @NonNull String value) {
        initParamsIfNeeded().put(key, value);
        return this;
    }

    @Override
    public PostFormBuilder headers(@NonNull Map<String, String> headers) {
        initHeadersIfNeeded().putAll(headers);
        return this;
    }

    @Override
    public PostFormBuilder addHeader(@NonNull String key, @NonNull String value) {
        initHeadersIfNeeded().put(key, value);
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
}