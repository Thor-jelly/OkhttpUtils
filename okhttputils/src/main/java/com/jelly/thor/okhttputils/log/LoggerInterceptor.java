package com.jelly.thor.okhttputils.log;

import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;

/**
 * 类描述：日志拦截器,网上搜索的一个<br/>
 * 建议直接使用OkHttp的拦截器implementation("com.squareup.okhttp3:logging-interceptor:4.7.2")
 * 创建人：吴冬冬<br/>
 * 创建时间：2018/5/15 10:46 <br/>
 */
public class LoggerInterceptor implements Interceptor {
    private static final String TAG = "OkHttpUtils";
    private String tag;
    private boolean showResponse;

    public LoggerInterceptor(String tag, boolean showResponse) {
        if (TextUtils.isEmpty(tag)) {
            tag = TAG;
        }
        this.showResponse = showResponse;
        this.tag = tag;
    }

    public LoggerInterceptor(String tag) {
        this(tag, true);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
//        logForRequest(request);
        Response response = chain.proceed(request);
        return logForResponse(response);
    }

    private Response logForResponse(Response response) {
        try {
            if (showResponse) {
                //===>response log
//            Log.d(tag, "========response'log=======");
                Request request = response.request();
                Log.d(tag, "url : " + request.url());
                Log.d(tag, "code : " + response.code());
//            Log.d(tag, "protocol : " + clone.protocol());
//            if (!TextUtils.isEmpty(clone.message()))
//                Log.d(tag, "message : " + clone.message());
                if (request.method().equals("POST")) {
                    StringBuilder sb = new StringBuilder();
                    if (request.body() instanceof FormBody) {
                        FormBody body = (FormBody) request.body();
                        for (int i = 0; i < body.size(); i++) {
                            sb.append(body.encodedName(i))
                                    .append("=")
                                    .append(body.encodedValue(i))
                                    .append(",");
                        }
                        sb.delete(sb.length() - 1, sb.length());
                    } else {
                        //暂时只有键值对和json格式
                        final Request copy = request.newBuilder().build();
                        final Buffer buffer = new Buffer();
                        try {
                            copy.body().writeTo(buffer);
                            String readUtf8 = buffer.readUtf8();
                            sb.append(readUtf8);
                        } catch (IOException e) {
                            sb.append("something error when show requestBody.");
                        }
                    }
                    Log.d(tag, "RequestParams:{" + sb.toString() + "}");
                }

                ResponseBody body = response.body();
                if (body != null) {
                    MediaType mediaType = body.contentType();
                    if (mediaType != null) {
//                        Log.d(tag, "responseBody's contentType : " + mediaType.toString());
                        if (isText(mediaType)) {
                            String resp = body.string();
                            String s = resp;
                            int segmentSize = 3 * 1024;
                            long length = s.length();
                            if (length <= segmentSize) {// 长度小于等于限制直接打印
                                Log.d(tag, "responseBody's content : " + s);
                            } else {
                                int currentInt = 0;
                                while (s.length() > segmentSize) {// 循环分段打印日志
                                    String logContent = s.substring(0, segmentSize);
                                    if (currentInt == 0) {
                                        currentInt++;
                                        Log.d(tag, "responseBody's content : " + logContent);
                                    } else {
                                        Log.d(tag, logContent);
                                    }
                                    s = s.replace(logContent, "");
                                }
                                Log.d(tag, s);// 打印剩余日志
                            }
                            body = ResponseBody.create(mediaType, resp);
                            return response.newBuilder().body(body).build();
                        } else {
                            Log.d(tag, "responseBody's content : " + " maybe [file part] , too large too print , ignored!");
                        }
                    }
                }
            }
//            Log.d(tag, "========response'log=======end");
        } catch (Exception e) {
//            e.printStackTrace();
        }

        return response;
    }

 /*   private void logForRequest(Request request) {
        try {
            String url = request.url().toString();
            Headers headers = request.headers();

            Log.d(tag, "========request'log=======");
            Log.d(tag, "method : " + request.method());
            Log.d(tag, "url : " + url);
            if (headers != null && headers.size() > 0) {
                Log.d(tag, "headers : " + headers.toString());
            }
            RequestBody requestBody = request.body();
            if (requestBody != null) {
                MediaType mediaType = requestBody.contentType();
                if (mediaType != null) {
                    Log.d(tag, "requestBody's contentType : " + mediaType.toString());
                    if (isText(mediaType)) {
                        Log.d(tag, "requestBody's content : " + bodyToString(request));
                    } else {
                        Log.d(tag, "requestBody's content : " + " maybe [file part] , too large too print , ignored!");
                    }
                }
            }
            Log.d(tag, "========request'log=======end");
        } catch (Exception e) {
//            e.printStackTrace();
        }
    }*/

    private boolean isText(MediaType mediaType) {
        if (mediaType.type() != null && mediaType.type().equals("text")) {
            return true;
        }
        if (mediaType.subtype() != null) {
            if (mediaType.subtype().equals("json") ||
                    mediaType.subtype().equals("xml") ||
                    mediaType.subtype().equals("html") ||
                    mediaType.subtype().equals("webviewhtml")
                    )
                return true;
        }
        return false;
    }

   /* private String bodyToString(final Request request) {
        try {
            final Request copy = request.newBuilder().build();
            final Buffer buffer = new Buffer();
            copy.body().writeTo(buffer);
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "something error when show requestBody.";
        }
    }*/
}
