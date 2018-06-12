package com.example.okhttputils.builder;

import com.example.okhttputils.request.RequestCall;
import com.example.okhttputils.request.WebSocketRequest;
import com.example.okhttputils.utils.Exceptions;

/**
 * 类描述：webSockerBuilder <br/>
 * 创建人：吴冬冬<br/>
 * 创建时间：2018/5/15 13:37 <br/>
 */
public class GetWebSocketBuilder extends OkHttpRequestBuilder<GetWebSocketBuilder> {
    @Override
    @Deprecated
    public RequestCall build() {
        Exceptions.illegalArgument("this method is deprecated, please use newBuild method!");
        return null;
    }

    public WebSocketRequest newBuild() {
        return new WebSocketRequest(url, tag, null,null, id, false, false);
    }
}
