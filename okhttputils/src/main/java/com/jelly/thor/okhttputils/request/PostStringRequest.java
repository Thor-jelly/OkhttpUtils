package com.jelly.thor.okhttputils.request;

import android.text.TextUtils;

import com.jelly.thor.okhttputils.OkHttpUtils;
import com.jelly.thor.okhttputils.builder.OkHttpRequestBuilder;
import com.jelly.thor.okhttputils.builder.PostStringBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * 类描述：post Json 网络请求 <br/>
 * 创建人：吴冬冬<br/>
 * 创建时间：2018/5/14 17:57 <br/>
 */
public class PostStringRequest extends OkHttpRequest {
    private static final MediaType NOW_MEDIA_TYPE = MediaType.parse("application/json; charset=utf-8");

    private final String strParams;

    public PostStringRequest(String url, OkHttpRequestBuilder<PostStringBuilder> okHttpRequestBuilder, String strParams) {
        super(url, okHttpRequestBuilder);
        this.strParams = strParams;
    }

    @Override
    protected RequestBody requestBody() {
        if (!TextUtils.isEmpty(strParams)) {
            return RequestBody.create(strParams, NOW_MEDIA_TYPE);
        }
        JSONObject jsonObject = new JSONObject();
        //添加请求参数
        addRequestParams(jsonObject, okHttpRequestBuilder.getParams());

        Map<String, String> commonParams = OkHttpUtils.getInstance().getCommonParams();
        //添加通用请求参数
        addRequestParams(jsonObject, commonParams);

        return RequestBody.create(NOW_MEDIA_TYPE, jsonObject.toString());
    }

    /**
     * 添加请求参数
     */
    private void addRequestParams(JSONObject jsonObject, Map<String, String> params) {
        if (params != null && !params.isEmpty()) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (value == null) {
                    throw new IllegalArgumentException("参数中的" + key + " 赋值为null");
                }
                if (value.startsWith("[") && value.endsWith("]")) {
                    try {
                        JSONArray newJSONArr = new JSONArray(value);
                        jsonObject.put(key, newJSONArr);
                    } catch (JSONException e) {
                        throw new IllegalArgumentException("post json 格式异常：" + e.getMessage());
                    }
                } else if (value.startsWith("{") && value.endsWith("}")) {
                    try {
                        JSONObject newObj = new JSONObject(value);
                        jsonObject.put(key, newObj);
                    } catch (JSONException e) {
                        throw new IllegalArgumentException("post json 格式异常：" + e.getMessage());
                    }
                } else {
                    try {
                        jsonObject.put(key, value);
                    } catch (JSONException e) {
                        throw new IllegalArgumentException("post json 格式异常：" + e.getMessage());
                    }
                }
            }
        }
    }
}