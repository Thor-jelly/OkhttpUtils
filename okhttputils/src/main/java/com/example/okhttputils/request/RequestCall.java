package com.example.okhttputils.request;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.okhttputils.BuildConfig;
import com.example.okhttputils.OkHttpUtils;
import com.example.okhttputils.callback.Callback;
import com.example.okhttputils.utils.CommentUtils;
import com.example.okhttputils.utils.ErrorCode;
import com.example.okhttputils.utils.Platform;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * 类描述：网络请求<br/>
 * 创建人：吴冬冬<br/>
 * 创建时间：2018/5/15 17:27 <br/>
 */
public class RequestCall {
    private OkHttpRequest mOkHttpRequest;
    private Request mRequest;
    private Call mCall;

    RequestCall(OkHttpRequest okHttpRequest) {
        this.mOkHttpRequest = okHttpRequest;
    }

    public void execute(final Callback callback) {
        callback.mOkHttpRequest = mOkHttpRequest;
        //判断是否有网络
        boolean isNet = CommentUtils.networkAvailable();
        if (!isNet) {
            //失败回调 主线程中
            sendOkHttpFail(mOkHttpRequest.getId(), ErrorCode.RESPONSE_NET, "当前没有网络！", callback);
            return;
        }

        //回调通知开始
        Platform.get().execute(new Runnable() {
            @Override
            public void run() {
                callback.onBefore(mOkHttpRequest.getId());
            }
        });

        OkHttpClient okHttpClient = OkHttpUtils.getInstance().getOkHttpClient();
        mRequest = mOkHttpRequest.getRequest();

        //动态改变的公共参数
        Map<String, String> changeCommonParameters = callback.addChangeCommonParameters();
        if (changeCommonParameters != null && !changeCommonParameters.isEmpty()) {
            HttpUrl.Builder newUrl = mRequest.url().newBuilder();
            for (Map.Entry<String, String> entry : changeCommonParameters.entrySet()) {
                newUrl.addQueryParameter(entry.getKey(), entry.getValue());
            }
            Request newRequest = mRequest.newBuilder().url(newUrl.build()).build();
            mCall = okHttpClient.newCall(newRequest);
        } else {
            mCall = okHttpClient.newCall(mRequest);
        }


        //OkHttpUtils.getInstance().execute(this, callback);
        mCall.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                //在子线程中
                if (BuildConfig.DEBUG) {
                    Log.d("OkHttpUtils", mOkHttpRequest.getId() + "-OkHttp3--->>>onFailure: " + (e == null ? "IOException 为null" : e.toString()));
                    Log.d("OkHttpUtils", mOkHttpRequest.getId() + "-OkHttp3--->>>onFailure: 当前网络是否被被取消=" + call.isCanceled());
                }
                if (!call.isCanceled()) {
                    call.cancel();
                }
                //失败回调 主线程中
                sendOkHttpFail(mOkHttpRequest.getId(), ErrorCode.RESPONSE_NET, "网络异常！", callback);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                final int id = mOkHttpRequest.getId();
                //在子线程中
                if (call.isCanceled()) {
                    //失败回调 主线程中
                    sendOkHttpFail(id, ErrorCode.RESPONSE_NET_CANCEL, "网络被取消！", callback);
                    return;
                }
                if (!response.isSuccessful()) {
                    int code = response.code();
                    String errorStr = response.message();
                    //失败回调 主线程中
                    sendOkHttpFail(id, code, errorStr, callback);
                    return;
                }

                Object o = null;
                try {
                    //数据解析需要在子线程
                    o = callback.parseNetworkResponse(response, id, mOkHttpRequest);
                } catch (IOException e) {
                    e.printStackTrace();
                    //失败回调 主线程中
                    sendOkHttpFail(id, ErrorCode.RESPONSE_NULL, "返回response异常,网址为：" + mRequest.url().toString(), callback);
                } catch (JSONException e) {
                    e.printStackTrace();
                    //失败回调 主线程中
                    sendOkHttpFail(id, ErrorCode.RESPONSE_OBJ, "数据解析异常，地址为：" + mRequest.url().toString(), callback);
                } catch (Exception e) {
                    e.printStackTrace();
                    //失败回调 主线程中
                    sendOkHttpFail(id, ErrorCode.RESPONSE_ERROR, e.toString()+"，地址为："+mRequest.url().toString(), callback);
                } finally {
                    ResponseBody body = response.body();
                    if (body != null) {
                        body.close();
                    }
                }

                //如果自定义没有返回null，如果返回null表示不需要执行成功回调onResponse onAfter
                //主线程中
                if (o != null) {
                    sendOkHttpSuccess(id, o, callback);
                } else {
                    sendOkHttpAfter(id, callback);
                }
            }
        });
    }

    private void sendOkHttpAfter(final int id, final Callback callback) {
        Platform.get().execute(new Runnable() {
            @Override
            public void run() {
                callback.onAfter(id);
            }
        });
    }

    private void sendOkHttpFail(final int id, final int code, final String errorStr, final Callback callback) {
        Platform.get().execute(new Runnable() {
            @Override
            public void run() {
                callback.onError(code, errorStr, id, mOkHttpRequest);
                callback.onAfter(id);
            }
        });
    }

    private void sendOkHttpSuccess(final int id, final Object o, final Callback callback) {
        //下面两个方法需要转换到主线程
        //解析完成的数据
        Platform.get().execute(new Runnable() {
            @Override
            public void run() {
                callback.onResponse(0, o, id);
                //完成
                callback.onAfter(id);
            }
        });
    }
}
