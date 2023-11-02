package com.jelly.thor.okhttputils.callback;

import com.jelly.thor.okhttputils.OkHttpUtils;
import com.jelly.thor.okhttputils.converters.Converter;
import com.jelly.thor.okhttputils.converters.IRefParamsType;
import com.jelly.thor.okhttputils.request.OkHttpRequest;

import okhttp3.Response;

/**
 * 类描述：当前项目数据初步解析 Gson写法<br/>
 * 创建人：吴冬冬<br/>
 * 创建时间：2022/4/27 11:25 <br/>
 */
public abstract class OkHttpCallback<T> extends Callback<T> implements IRefParamsType<T> {
    @Override
    public T parseNetworkResponse(Response response, int id, OkHttpRequest okHttpRequest) throws Exception {
        Converter.Factory converterFactory = OkHttpUtils.getInstance().getConverterFactory();
        T parseData = converterFactory.responseBodyConverter(id, this, response.request(), response, null);
        return parseData;
    }
}