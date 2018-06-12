package com.example.okhttputils.builder;

import com.example.okhttputils.request.PostFormRequest;
import com.example.okhttputils.request.RequestCall;

import java.util.Map;

/**
 * 类描述：post build <br/>
 * 创建人：吴冬冬<br/>
 * 创建时间：2018/5/14 17:54 <br/>
 */
public class PostFormBuilder extends OkHttpRequestBuilder<PostFormBuilder> implements HasParamsable{
    @Override
    public OkHttpRequestBuilder params(Map<String, String> params) {
        this.params = params;
        return this;
    }

    @Override
    public RequestCall build() {
        return new PostFormRequest(url, tag, params, headers, id, isShowDialog, isShowToast).build();
    }
}
