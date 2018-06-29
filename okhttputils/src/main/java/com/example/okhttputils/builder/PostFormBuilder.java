package com.example.okhttputils.builder;

import android.webkit.URLUtil;

import com.example.okhttputils.OkHttpUtils;
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
        String myUrl;
        if (baseUrl != null) {
            if (URLUtil.isValidUrl(url)) {
                myUrl = url;
            }else {
                myUrl = baseUrl + url;
            }
        }else if (OkHttpUtils.getInstance().getBaseUrl() != null) {
            if (URLUtil.isValidUrl(url)) {
                myUrl = url;
            }else {
                myUrl = OkHttpUtils.getInstance().getBaseUrl() + url;
            }
        }else {
            myUrl = url;
        }
        return new PostFormRequest(myUrl, tag, params, headers, id, isShowDialog, isShowToast).build();
    }
}
