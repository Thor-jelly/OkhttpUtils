package com.jelly.thor.okhttputils.request;

import java.util.Map;

import okhttp3.RequestBody;

/**
 * 类描述：get网络请求<br/>
 * 创建人：吴冬冬<br/>
 * 创建时间：2018/5/14 15:24 <br/>
 */
public class GetRequest extends OkHttpRequest {
    public GetRequest(String url, Object tag, Map<String, String> headers, int id, boolean isShowDialog, boolean isShowToast) {
        super(url, tag, headers, id, isShowDialog, isShowToast);
    }

    @Override
    protected RequestBody requestBody() {
        return null;
    }
}
