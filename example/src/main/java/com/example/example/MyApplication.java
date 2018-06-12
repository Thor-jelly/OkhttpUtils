package com.example.example;

import android.app.Application;

import com.example.okhttputils.OkHttpUtils;
import com.example.okhttputils.https.HttpsUtils;
import com.example.okhttputils.log.LoggerInterceptor;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

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
//                        CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
//                        CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
//                        CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256)
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

        OkHttpUtils.initClient(okHttpClient);
    }
}
