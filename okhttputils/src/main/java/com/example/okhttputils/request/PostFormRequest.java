package com.example.okhttputils.request;

import java.util.Map;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * 类描述：post网络请求 <br/>
 * 创建人：吴冬冬<br/>
 * 创建时间：2018/5/14 17:57 <br/>
 */
public class PostFormRequest extends OkHttpRequest {
    public PostFormRequest(String url, Object tag, Map<String, String> params, Map<String, String> headers, int id, boolean isShowDialog, boolean isShowToast) {
        super(url, tag, params, headers, id, isShowDialog, isShowToast);
    }

    @Override
    protected RequestBody requestBody() {
        FormBody.Builder formBody = new FormBody.Builder();
        for (String key : params.keySet()) {
            formBody.add(key, params.get(key));
        }
        return formBody.build();
    }
}
