package com.jelly.thor.example;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.webkit.WebSettings;

import com.jelly.thor.example.netserver.ParseDataImpl;
import com.jelly.thor.okhttputils.OkHttpUtils;
import com.jelly.thor.okhttputils.https.HttpsUtils;
import com.jelly.thor.okhttputils.log.LoggerInterceptor;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.CipherSuite;
import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.TlsVersion;

/**
 * 创建人：吴冬冬<br/>
 * 创建时间：2018/5/16 10:23 <br/>
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        //下面代码看https://github.com/square/okhttp/wiki/HTTPS
        //https用ConnectionSpec.MODERN_TLS，不是的就用ConnectionSpec.CLEARTEXT
//        ConnectionSpec spec = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
//                .tlsVersions(TlsVersion.TLS_1_3,
//                        TlsVersion.TLS_1_2)
//                .cipherSuites(
//                        // TLSv1.3.
//                        CipherSuite.TLS_AES_128_GCM_SHA256,
//                        CipherSuite.TLS_AES_256_GCM_SHA384,
//                        CipherSuite.TLS_CHACHA20_POLY1305_SHA256,
//
//                        // TLSv1.0, TLSv1.1, TLSv1.2.
//                        CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
//                        CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
//                        CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384,
//                        CipherSuite.TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384,
//                        CipherSuite.TLS_ECDHE_ECDSA_WITH_CHACHA20_POLY1305_SHA256,
//                        CipherSuite.TLS_ECDHE_RSA_WITH_CHACHA20_POLY1305_SHA256,
//
//                        // Note that the following cipher suites are all on HTTP/2's bad cipher suites list. We'll
//                        // continue to include them until better suites are commonly available.
//                        CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA,
//                        CipherSuite.TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA,
//                        CipherSuite.TLS_RSA_WITH_AES_128_GCM_SHA256,
//                        CipherSuite.TLS_RSA_WITH_AES_256_GCM_SHA384,
//                        CipherSuite.TLS_RSA_WITH_AES_128_CBC_SHA,
//                        CipherSuite.TLS_RSA_WITH_AES_256_CBC_SHA,
//                        CipherSuite.TLS_RSA_WITH_3DES_EDE_CBC_SHA)
//                .build();

        ConnectionSpec spec = new ConnectionSpec.Builder(ConnectionSpec.CLEARTEXT)
                .build();

        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(null, null, null);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new LoggerInterceptor("TAG"))
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                .writeTimeout(10000L, TimeUnit.MILLISECONDS)
                .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
                .connectionSpecs(Collections.singletonList(spec))
                .hostnameVerifier(new HttpsUtils.UnSafeHostnameVerifier())
                .build();

        OkHttpUtils.initClient(okHttpClient).setIParseData(new ParseDataImpl());

        //OkHttpUtils.getInstance().setBaseUrl("https://b.shandian.net/pos/");
        LinkedHashMap<String, String> m = new LinkedHashMap<>();
        m.put("User-Agent", getUserAgent(this));
        OkHttpUtils.getInstance().addCommonHeaders(m);
    }


    private static String getUserAgent(Context context) {
        String userAgent = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            try {
                userAgent = WebSettings.getDefaultUserAgent(context);
            } catch (Exception e) {
                userAgent = System.getProperty("http.agent");
            }
        } else {
            userAgent = System.getProperty("http.agent");
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0, length = userAgent.length(); i < length; i++) {
            char c = userAgent.charAt(i);
            if (c <= '\u001f' || c >= '\u007f') {
                sb.append(String.format("\\u%04x", (int) c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
