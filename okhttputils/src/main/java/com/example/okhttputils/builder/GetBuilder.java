package com.example.okhttputils.builder;

import android.net.Uri;

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
        if (params != null) {
            url = appendParams(url, params);
        }
        return new GetRequest(url, tag, headers, id, isShowDialog, isShowToast).build();
    }

    /**
     * 拼接url和参数
     * @param url
     * @param params
     * @return
     */
    protected String appendParams(String url, Map<String, String> params) {
        if (url == null || params == null || params.isEmpty()) {
            return url;
        }
        Uri.Builder builder = Uri.parse(url).buildUpon();
        Set<String> keys = params.keySet();
        Iterator<String> iterator = keys.iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
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
