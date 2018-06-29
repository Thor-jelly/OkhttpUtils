package com.example.okhttputils.builder;

import android.net.Uri;
import android.webkit.URLUtil;

import com.example.okhttputils.OkHttpUtils;
import com.example.okhttputils.request.GetRequest;
import com.example.okhttputils.request.RequestCall;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * 类描述：getBuilder <br/>
 * 创建人：吴冬冬<br/>
 * 创建时间：2018/5/14 15:05 <br/>
 */
public class GetBuilder extends OkHttpRequestBuilder<GetBuilder> implements HasParamsable{
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
        if (params != null) {
            myUrl = appendParams(myUrl, params);
        }
        return new GetRequest(myUrl, tag, headers, id, isShowDialog, isShowToast).build();
    }

    /**
     * 拼接url和参数
     * @param url
     * @param params
     * @return
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
    public GetBuilder params(Map<String, String> params) {
        this.params = params;
        return this;
    }

//   @Override
//    public GetBuilder addParams(String key, String val) {
//        if (this.params == null) {
//            params = new LinkedHashMap<>();
//        }
//        params.put(key, val);
//        return this;
//    }
}