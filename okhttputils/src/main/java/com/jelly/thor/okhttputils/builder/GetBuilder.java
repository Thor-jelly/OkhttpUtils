package com.jelly.thor.okhttputils.builder;

import com.jelly.thor.okhttputils.OkHttpUtils;
import com.jelly.thor.okhttputils.request.GetRequest;
import com.jelly.thor.okhttputils.request.RequestCall;

import java.util.Map;

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

    @Override
    public GetBuilder params(@NonNull Map<String, String> params) {
        initParamsIfNeeded().putAll(params);
        return this;
    }

    @Override
    public GetBuilder addParam(@NonNull String key, @NonNull String value) {
        initParamsIfNeeded().put(key, value);
        return this;
    }

    @Override
    public GetBuilder headers(@NonNull Map<String, String> headers) {
        initHeadersIfNeeded().putAll(headers);
        return this;
    }

    @Override
    public GetBuilder addHeader(@NonNull String key, @NonNull String value) {
        initHeadersIfNeeded().put(key, value);
        return this;
    }
}
