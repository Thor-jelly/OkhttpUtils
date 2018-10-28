package com.jelly.thor.okhttputils.request;

import com.jelly.thor.okhttputils.OkHttpUtils;

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
        //添加请求参数
        addRequestParams(formBody, params);

        Map<String, String> commonParams = OkHttpUtils.getInstance().getCommonParams();
        //添加通用请求参数
        addRequestParams(formBody, commonParams);

        return formBody.build();
    }

    /**
     * 添加请求参数
     */
    private void addRequestParams(FormBody.Builder formBody, Map<String, String> params) {
        if (params != null && !params.isEmpty()) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (value == null) {
                    throw new IllegalArgumentException("参数中的" + key + " 赋值为null");
                }
                formBody.add(key, value);
            }
        }
    }
}
