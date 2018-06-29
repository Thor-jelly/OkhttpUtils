package com.example.okhttputils;



import com.example.okhttputils.builder.GetBuilder;
import com.example.okhttputils.builder.GetWebSocketBuilder;
import com.example.okhttputils.builder.PostFileBuilder;
import com.example.okhttputils.builder.PostFormBuilder;
import com.example.okhttputils.tag.TagBeen;

import okhttp3.Call;
import okhttp3.OkHttpClient;

/**
 * 类描述：okHttp 工具类地址 <br/>
 * 创建人：吴冬冬<br/>
 * 创建时间：2018/5/11 11:22 <br/>
 */
public class OkHttpUtils {
    private volatile static OkHttpUtils mInstance;
    private OkHttpClient mOkHttpClient;
    private String baseUrl;

    private OkHttpUtils(OkHttpClient okHttpClient) {
        if (okHttpClient == null) {
            mOkHttpClient = new OkHttpClient();
        } else {
            mOkHttpClient = okHttpClient;
        }
    }

    public static OkHttpUtils initClient(OkHttpClient okHttpClient) {
        if (mInstance == null) {
            synchronized (OkHttpUtils.class) {
                if (mInstance == null) {
                    mInstance = new OkHttpUtils(okHttpClient);
                }
            }
        }
        return mInstance;
    }

    public static OkHttpUtils getInstance() {
        return initClient(null);
    }

    /**
     * 获取okHttpClient
     */
    public OkHttpClient getOkHttpClient() {
        if (mOkHttpClient == null) {
            throw new Error("请先在Application中初始化OkHttpClient, 调用OkHttpUtils.getInstance()或者OkHttpUtils.initClient()方法!");
        }
        return mOkHttpClient;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    /**
     * 取消对应tag网络网络请求
     */
    public void cancelTag(Object tag) {
        if (mOkHttpClient == null) {
            throw new Error("请先初始化OkHttpClient, 调用OkHttpUtils.getInstance()或者OkHttpUtils.initClient()方法!");
        }
        for (Call call : mOkHttpClient.dispatcher().queuedCalls()) {
            if (call.isCanceled()) {
                continue;
            }
            Object tagTag = call.request().tag();
            if (tagTag instanceof TagBeen) {
                TagBeen tagBeen = (TagBeen) tagTag;
                if (tag.equals(tagBeen.getTag())) {
                    call.cancel();
                }
            }
        }
        for (Call call : mOkHttpClient.dispatcher().runningCalls()) {
            if (call.isCanceled()) {
                continue;
            }
            Object tagTag = call.request().tag();
            if (tagTag instanceof TagBeen) {
                TagBeen tagBeen = (TagBeen) tagTag;
                if (tag.equals(tagBeen.getTag())) {
                    call.cancel();
                }
            }
        }
    }

    public static GetWebSocketBuilder WebSocket() {
        return new GetWebSocketBuilder();
    }

    /**
     * get请求
     */
    public static GetBuilder get() {
        return new GetBuilder();
    }

    /**
     * post请求
     */
    public static PostFormBuilder post() {
        return new PostFormBuilder();
    }

    /**
     * 上传文件请求
     */
    public static PostFileBuilder postFile() {
        return new PostFileBuilder();
    }
}
