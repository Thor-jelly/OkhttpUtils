package com.jelly.thor.okhttputils.builder;

import com.jelly.thor.okhttputils.request.RequestCall;
import com.jelly.thor.okhttputils.request.WebSocketRequest;
import com.jelly.thor.okhttputils.utils.Exceptions;

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
        return new WebSocketRequest(url, this.isShowDialog(false).isShowToast(false));
    }
}
