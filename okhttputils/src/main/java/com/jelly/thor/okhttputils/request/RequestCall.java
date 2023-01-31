package com.jelly.thor.okhttputils.request;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.jelly.thor.okhttputils.OkHttpUtils;
import com.jelly.thor.okhttputils.callback.Callback;
import com.jelly.thor.okhttputils.callback.ParseDataUtils;
import com.jelly.thor.okhttputils.utils.CommontUtils;
import com.jelly.thor.okhttputils.utils.ErrorCode;
import com.jelly.thor.okhttputils.utils.Platform;
import com.jushuitan.jht.basemodule.utils.net.exception.ResponseException;
import com.jushuitan.jht.basemodule.utils.net.exception.ServerException;

import java.io.IOException;
import java.util.Map;

import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.MaybeEmitter;
import io.reactivex.rxjava3.core.MaybeOnSubscribe;
import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 类描述：网络请求<br/>
 * 创建人：吴冬冬<br/>
 * 创建时间：2018/5/15 17:27 <br/>
 */
public class RequestCall {
    private final OkHttpRequest mOkHttpRequest;
    private Request mRequest;
    private Call mCall;

    RequestCall(OkHttpRequest okHttpRequest) {
        this.mOkHttpRequest = okHttpRequest;
    }

    /**
     * rxJava模式
     */
    public Maybe<Response> getRxJava() {
        return Maybe.create(new MaybeOnSubscribe<Response>() {
            @Override
            public void subscribe(@NonNull MaybeEmitter<Response> emitter) throws Throwable {
                if (emitter.isDisposed()) {
                    return;
                }
                execute(emitter, null);
            }
        });
    }

    /**
     * java模式
     */
    public <T> void execute(Callback<T> callback) {
        execute(null, callback);
    }

    private <T> void execute(@Nullable MaybeEmitter<Response> emitter, @Nullable Callback<T> callback) {
        //判断是否有网络
        boolean isNet = CommontUtils.networkAvailable();
        if (!isNet) {
            String errorStr = "当前没有网络！";
            if (emitter == null) {
                callback.mOkHttpRequest = mOkHttpRequest;
                //失败回调 主线程中
                sendOkHttpFail(mOkHttpRequest.id, ErrorCode.NET_ERROR, errorStr, callback);
            } else {
                if (emitter.isDisposed()) {
                    return;
                }
                emitter.onError(new ServerException(ErrorCode.NET_ERROR, errorStr));
            }
            return;
        }

        //回调通知开始
        if (callback != null) {
            callback.mOkHttpRequest = mOkHttpRequest;
            Platform.get().execute(new Runnable() {
                @Override
                public void run() {
                    callback.onBefore(mOkHttpRequest.getId());
                }
            });
        }

        OkHttpClient okHttpClient = OkHttpUtils.getInstance().getOkHttpClient();

        try {
            mRequest = mOkHttpRequest.getRequest();
        } catch (IllegalArgumentException e) {
            //失败回调 主线程中
            sendOkHttpFail(mOkHttpRequest.getId(), ErrorCode.PARAMS_EXCEPTION, mRequest.url().toString() + "添加参数异常 " + e.getMessage(), callback);
            return;
        }

        //动态追加的公共参数
        Map<String, String> changeCommonParameters = callback == null ? null : callback.addChangeCommonParameters();
        if (changeCommonParameters != null && !changeCommonParameters.isEmpty()) {
            String method = mRequest.method();
//            switch (method) {
//                case "POST":
//                    RequestBody body = mRequest.body();
//                    if (body instanceof FormBody) {
//                        //键值对形式
//                        FormBody.Builder newFormBody = new FormBody.Builder();
//                        FormBody oldFormBody = (FormBody) body;
//                        //把旧的添加进新的
//                        for (int i = 0; i < oldFormBody.size(); i++) {
//                            newFormBody.add(oldFormBody.encodedName(i), oldFormBody.encodedValue(i));
//                        }
//                        //添加新的
//                        for (Map.Entry<String, String> entry : changeCommonParameters.entrySet()) {
//                            newFormBody.add(entry.getKey(), entry.getValue());
//                        }
//                        //重新创建一个新的request
//                        FormBody build = newFormBody.build();
//                        Request newRequest = mRequest.newBuilder()
//                                .post(build)
//                                .build();
//                        mCall = okHttpClient.newCall(newRequest);
//                    } else {
//                        //暂时只有json格式
//
//                    }
//                    break;
//                default:
            HttpUrl.Builder newUrl = mRequest.url().newBuilder();
            for (Map.Entry<String, String> entry : changeCommonParameters.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (value == null) {
                    sendOkHttpFail(mOkHttpRequest.getId(), ErrorCode.PARAMS_EXCEPTION, mRequest.url().toString() + "addChangeCommonParameters参数异常 " + "参数中的" + key + " 赋值为null", callback);
                    return;
                }
                newUrl.addQueryParameter(key, value);
            }
            Request newRequest = mRequest.newBuilder().url(newUrl.build()).build();
            mCall = okHttpClient.newCall(newRequest);
//                    break;
//            }
        } else {
            mCall = okHttpClient.newCall(mRequest);
        }


        //OkHttpUtils.getInstance().execute(this, callback);
        mCall.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                //在子线程中
//                if (BuildConfig.DEBUG) {
//                    Log.d("OkHttpUtils", mOkHttpRequest.getId() + "-OkHttp3--->>>onFailure: " + (e == null ? "IOException 为null" : e.toString()));
//                    Log.d("OkHttpUtils", mOkHttpRequest.getId() + "-OkHttp3--->>>onFailure: 当前网络是否被被取消=" + call.isCanceled());
//                }
                if (!call.isCanceled()) {
                    call.cancel();
                }
                //失败回调 主线程中
                sendOkHttpFail(mOkHttpRequest.getId(), ErrorCode.NET_ERROR, "网络异常！", callback);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                final int id = mOkHttpRequest.id;
                //在子线程中
                if (call.isCanceled()) {
                    String errorStr = "网络被取消！";
                    if (emitter == null) {
                        //失败回调 主线程中
                        sendOkHttpFail(id, ErrorCode.NET_CANCEL, errorStr, callback);
                    } else {
                        if (emitter.isDisposed()) {
                            return;
                        }
                        emitter.onError(new ServerException(ErrorCode.NET_CANCEL, errorStr));
                    }
                    return;
                }
                if (!response.isSuccessful()) {
                    int code = response.code();
                    String errorStr = response.message();
                    if (TextUtils.isEmpty(errorStr)) {
                        errorStr = response.toString();
                    }
                    if (emitter == null) {
                        //失败回调 主线程中
                        sendOkHttpFail(id, code, errorStr, callback);
                    } else {
                        if (emitter.isDisposed()) {
                            return;
                        }
                        emitter.onError(new ServerException(code, errorStr));
                    }
                    return;
                }

                if (emitter == null) {
                    Object o = null;
                    try {
                        //数据解析需要在子线程
                        o = callback.parseNetworkResponse(response, id, mOkHttpRequest);
                    } catch (Exception e) {
                        //e.printStackTrace();
                        ResponseException responseException = ParseDataUtils.handleError(e, response.request());
                        //失败回调 主线程中
                        sendOkHttpFail(id, responseException.getCode(), responseException.getMessage(), callback);
                    } /*finally {
                        ResponseBody body = response.body();
                        if (body != null) {
                            body.close();
                        }
                    }*/

                    //如果自定义没有返回null，如果返回null表示不需要执行成功回调onResponse onAfter
                    //主线程中
                    if (o != null) {
                        sendOkHttpSuccess(id, o, response.request(), callback);
                    } else {
                        sendOkHttpAfter(id, callback);
                    }
                } else {
                    if (emitter.isDisposed()) {
                        return;
                    }
                    emitter.onSuccess(response);
                }
            }
        });
    }

    private <T> void sendOkHttpAfter(final int id, final Callback<T> callback) {
        Platform.get().execute(new Runnable() {
            @Override
            public void run() {
                if (callback == null) {
                    return;
                }
                callback.onAfter(id);
            }
        });
    }

    private <T> void sendOkHttpFail(final int id, final int code, final String errorStr, final Callback<T> callback) {
        Platform.get().execute(new Runnable() {
            @Override
            public void run() {
                if (callback == null) {
                    return;
                }
                callback.onError(code, errorStr, id, mOkHttpRequest);
                callback.onAfter(id);
            }
        });
    }

    private <T> void sendOkHttpSuccess(int id, Object o, Request request, Callback<T> callback) {
        //下面两个方法需要转换到主线程
        //解析完成的数据
        Platform.get().execute(new Runnable() {
            @Override
            public void run() {
                if (callback == null) {
                    return;
                }
                try {
                    callback.onResponse(0, (T) o, id);
                } catch (Exception e) {
                    sendOkHttpFail(id, ErrorCode.HANDLE_SUCCESS_ERROR, "在处理数据中失败：" + e.getMessage(), callback);
                }
                //完成
                callback.onAfter(id);
            }
        });
    }
}
