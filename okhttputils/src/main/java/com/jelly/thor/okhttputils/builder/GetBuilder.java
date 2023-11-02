package com.jelly.thor.okhttputils.builder;

import android.net.Uri;

import com.jelly.thor.okhttputils.OkHttpUtils;
import com.jelly.thor.okhttputils.request.GetRequest;
import com.jelly.thor.okhttputils.request.RequestCall;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import androidx.annotation.NonNull;

/**
 * 类描述：getBuilder <br/>
 * 创建人：吴冬冬<br/>
 * 创建时间：2018/5/14 15:05 <br/>
 */
public class GetBuilder extends OkHttpRequestBuilder<GetBuilder> implements HasParameters<GetBuilder>, HasHeaders<GetBuilder> {
    @Override
    public RequestCall build() {
        //处理请求
        String newUrl = getNewUrl();
        //设置公用请求参数
        Map<String, String> commonParams = OkHttpUtils.getInstance().getCommonParams();
        if (null != params) {
            if (commonParams != null && !commonParams.isEmpty()) {
                params.putAll(commonParams);
            }
            newUrl = appendParams(newUrl, params);
        } else {
            if (commonParams != null && !commonParams.isEmpty()) {
                newUrl = appendParams(newUrl, commonParams);
            }
        }
        return new GetRequest(newUrl, this).build();
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
        if (this.params == null) {
            this.params = new LinkedHashMap<>();
        }
        this.params.putAll(params);
        return this;
    }

    @Override
    public GetBuilder addParam(@NonNull String key, @NonNull String value) {
        if (this.params == null) {
            this.params = new LinkedHashMap<>();
        }
        params.put(key, value);
        return this;
    }

    @Override
    public GetBuilder headers(@NonNull Map<String, String> headers) {
        if (this.headers == null) {
            this.headers = new LinkedHashMap<>();
        }
        this.headers.putAll(headers);
        return this;
    }

    @Override
    public GetBuilder addHeader(@NonNull String key, @NonNull String value) {
        if (this.headers == null) {
            this.headers = new LinkedHashMap<>();
        }
        this.headers.put(key, value);
        return this;
    }
}
