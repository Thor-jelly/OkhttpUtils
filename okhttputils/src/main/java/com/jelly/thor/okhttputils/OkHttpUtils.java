package com.jelly.thor.okhttputils;


import androidx.annotation.NonNull;

import com.jelly.thor.okhttputils.builder.GetBuilder;
import com.jelly.thor.okhttputils.builder.GetWebSocketBuilder;
import com.jelly.thor.okhttputils.builder.PostFileBuilder;
import com.jelly.thor.okhttputils.builder.PostFormBuilder;
import com.jelly.thor.okhttputils.builder.PostStringBuilder;
import com.jelly.thor.okhttputils.callback.IParseData;
import com.jelly.thor.okhttputils.converters.Converter;
import com.jelly.thor.okhttputils.converters.gson.GsonConverterFactory;
import com.jelly.thor.okhttputils.tag.TagModel;

import java.util.LinkedHashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.OkHttpClient;

/**
 * 类描述：okHttp 工具类地址 <br/>
 * 创建人：吴冬冬<br/>
 * 创建时间：2018/5/11 11:22 <br/>
 */
public class OkHttpUtils {
    private OkHttpUtils() {
    }

    private static class Holder {
        private static final OkHttpUtils H = new OkHttpUtils();
    }

    public static OkHttpUtils getInstance() {
        return Holder.H;
    }


    private OkHttpClient mOkHttpClient;
    /**
     * 公共的url
     */
    private String mBaseUrl;
    /**
     * 通用请求参数
     */
    private Map<String, String> mCommonParams;
    /**
     * 通用请求头
     */
    private Map<String, String> mCommonHeaders;
    /**
     * 数据解析类
     */
    private IParseData mIParseData;
    /**
     * 数据解析方案 默认fastJson
     */
    private Converter.Factory mConverterFactory;

    /**
     * 在Application中初始化 网络
     */
    public static OkHttpUtils initClient(OkHttpClient okHttpClient) {
        OkHttpUtils h = getInstance();
        h.setOkhttpClient(okHttpClient);
        return h;
    }

    /**
     * 设置okhttp client
     */
    private void setOkhttpClient(OkHttpClient okHttpClient) {
        if (okHttpClient == null) {
            mOkHttpClient = new OkHttpClient();
        } else {
            mOkHttpClient = okHttpClient;
        }
    }

    /**
     * 获取okHttpClient
     */
    public OkHttpClient getOkHttpClient() {
        if (mOkHttpClient == null) {
            throw new Error("请先在Application中初始化OkHttpClient, 调用OkHttpUtils.getInstance()或者OkHttpUtils.initClient()方法!");
        }
        if (mIParseData == null) {
            throw new Error("请先在Application中初始化OkHttpClient, 调用OkHttpUtils.getInstance().setIParseData()或者OkHttpUtils.initClient()方法!");
        }
        return mOkHttpClient;
    }

    /**
     * 添加基本url
     */
    public OkHttpUtils setBaseUrl(@NonNull String mBaseUrl) {
        this.mBaseUrl = mBaseUrl;
        return this;
    }

    /**
     * 获取基本url
     */
    public String getBaseUrl() {
        return mBaseUrl;
    }

    /**
     * 添加全局公共请求参数
     */
    public OkHttpUtils addCommonParams(@NonNull Map<String, String> commonParams) {
        if (commonParams.isEmpty()) {
            return this;
        }
        if (mCommonParams == null) {
            mCommonParams = new LinkedHashMap<>();
        }
        mCommonParams.putAll(commonParams);
        return this;
    }

    /**
     * 获取全局公共请求参数
     */
    public Map<String, String> getCommonParams() {
        return mCommonParams;
    }

    /**
     * 添加全局公共请求参数
     */
    public OkHttpUtils addCommonHeaders(@NonNull Map<String, String> commonHeaders) {
        if (commonHeaders.isEmpty()) {
            return this;
        }
        if (mCommonHeaders == null) {
            mCommonHeaders = new LinkedHashMap<>();
        }
        mCommonHeaders.putAll(commonHeaders);
        return this;
    }

    /**
     * 获取全局公共请求头
     */
    public Map<String, String> getCommonHeaders() {
        return mCommonHeaders;
    }

    /**
     * 设置解析数据处理
     */
    public OkHttpUtils setIParseData(IParseData iParseData) {
        this.mIParseData = iParseData;
        return this;
    }

    public IParseData getParseData() {
        return mIParseData;
    }

    /**
     * 设置解析数据处理 默认fastJson处理解析
     */
    public OkHttpUtils setConverterFactory(Converter.Factory converterFactory) {
        this.mConverterFactory = converterFactory;
        return this;
    }

    public Converter.Factory getConverterFactory() {
        if (mConverterFactory == null) {
            //setConverterFactory(FastJsonConverterFactory.create());
            setConverterFactory(GsonConverterFactory.create());
        }
        return mConverterFactory;
    }

    /**
     * 取消所有网络请求
     */
    public void cancelAll() {
        if (mOkHttpClient == null) {
            throw new Error("请先初始化OkHttpClient, 调用OkHttpUtils.getInstance()或者OkHttpUtils.initClient()方法!");
        }
        for (Call call : mOkHttpClient.dispatcher().queuedCalls()) {
            if (call.isCanceled()) {
                continue;
            }
            call.cancel();
        }
        for (Call call : mOkHttpClient.dispatcher().runningCalls()) {
            if (call.isCanceled()) {
                continue;
            }
            call.cancel();
        }
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
            //取消tag逻辑
            cancelTagMethod(tag, call, tagTag);
        }
        for (Call call : mOkHttpClient.dispatcher().runningCalls()) {
            if (call.isCanceled()) {
                continue;
            }
            Object tagTag = call.request().tag();
            //取消tag逻辑
            cancelTagMethod(tag, call, tagTag);
        }
    }

    /**
     * 取消tag逻辑
     */
    private void cancelTagMethod(Object tag, Call call, Object tagTag) {
        if (tagTag instanceof TagModel) {
            Object saveTag = ((TagModel) tagTag).getTag();
            boolean tagIsString = tag instanceof String;
            boolean saveTagIsString = saveTag instanceof String;
            if (tagIsString
                    && saveTagIsString) {
                if (tag.equals(saveTag)) {
                    call.cancel();
                }
            } else if (!tagIsString && !saveTagIsString) {
                if (tag == saveTag) {
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
     * post请求 json传递
     */
    public static PostStringBuilder postString() {
        return new PostStringBuilder();
    }

    /**
     * 上传文件请求
     */
    public static PostFileBuilder postFile() {
        return new PostFileBuilder();
    }
}
