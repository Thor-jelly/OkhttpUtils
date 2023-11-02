package com.jelly.thor.okhttputils.builder;

import androidx.annotation.NonNull;

import com.jelly.thor.okhttputils.request.PostStringRequest;
import com.jelly.thor.okhttputils.request.RequestCall;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 类描述：post Json build <br/>
 * 创建人：吴冬冬<br/>
 * 创建时间：2018/5/14 17:54 <br/>
 */
public class PostStringBuilder extends OkHttpRequestBuilder<PostStringBuilder> implements HasParameters<PostStringBuilder>, HasHeaders<PostStringBuilder> {
    //工具类外转换完直接可以用的json
    private String strParams;

    //json 所有参数，设置完其他添加参数方法失效
    public PostStringBuilder params(@NonNull String strParams) {
        this.strParams = strParams;
        return this;
    }

    @Override
    public PostStringBuilder params(@NonNull Map<String, String> params) {
        if (this.params == null) {
            this.params = new LinkedHashMap<>();
        }
        this.params.putAll(params);
        return this;
    }

    @Override
    public PostStringBuilder addParam(@NonNull String key, @NonNull String value) {
        if (this.params == null) {
            params = new LinkedHashMap<>();
        }
        params.put(key, value);
        return this;
    }

    @Override
    public PostStringBuilder headers(@NonNull Map<String, String> headers) {
        if (this.headers == null) {
            this.headers = new LinkedHashMap<>();
        }
        this.headers.putAll(headers);
        return this;
    }

    @Override
    public PostStringBuilder addHeader(@NonNull String key, @NonNull String value) {
        if (this.headers == null) {
            this.headers = new LinkedHashMap<>();
        }
        this.headers.put(key, value);
        return this;
    }

    @Override
    public RequestCall build() {
        String myUrl = getNewUrl();
        return new PostStringRequest(myUrl, this, strParams).build();
    }
}