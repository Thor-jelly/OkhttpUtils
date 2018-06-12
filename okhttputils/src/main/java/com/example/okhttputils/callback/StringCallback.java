package com.example.okhttputils.callback;

import com.example.okhttputils.request.OkHttpRequest;

import java.io.IOException;

import okhttp3.Response;

/**
 * 类描述：自定义callback 返回string类型 <br/>
 * 创建人：吴冬冬<br/>
 * 创建时间：2018/5/15 17:22 <br/>
 */
public abstract class StringCallback extends Callback<String>
{
    @Override
    public String parseNetworkResponse(Response response, int id, OkHttpRequest okHttpRequest) throws IOException
    {
        return response.body().string();
    }
}
