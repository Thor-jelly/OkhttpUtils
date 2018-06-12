package com.example.okhttputils.request;

import com.example.okhttputils.OkHttpUtils;
import com.example.okhttputils.utils.Exceptions;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.WebSocketListener;

/**
 * 类描述：webSocket请求 <br/>
 * 创建人：吴冬冬<br/>
 * 创建时间：2018/5/15 13:51 <br/>
 */
public class WebSocketRequest extends OkHttpRequest {
    private static final Dispatcher DISPATCHER = new Dispatcher();

    public WebSocketRequest(String url, Object tag, Map<String, String> params, Map<String, String> headers, int id, boolean isShowDialog, boolean isShowToast) {
        super(url, tag, params, headers, id, isShowDialog, isShowToast);
        this.newBuild();
    }

    @Override
    protected RequestBody requestBody() {
        return null;
    }

    @Override
    @Deprecated
    public RequestCall build() {
        Exceptions.illegalArgument("this method is deprecated, please use newBuild method!");
        return null;
    }

    public WebSocketRequest newBuild() {
        return this;
    }

    public void execute(WebSocketListener webSocketListener) {
        OkHttpClient okHttpClient = OkHttpUtils.getInstance().getOkHttpClient()
                .newBuilder()
                .dispatcher(DISPATCHER)
                .pingInterval(60, TimeUnit.SECONDS)
                .build();
        okHttpClient.dispatcher().cancelAll();
        okHttpClient.dispatcher().runningCallsCount();
        Request request = new Request.Builder().url(url).build();
        okHttpClient.newWebSocket(request, webSocketListener);
        //okHttpClient.dispatcher().executorService().shutdown();
    }
}
