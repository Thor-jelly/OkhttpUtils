# 权限

```
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

# 配置OkhttpClient

```
OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .addInterceptor(new LoggerInterceptor("TAG"))
                  .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                  .readTimeout(10000L, TimeUnit.MILLISECONDS)
                  //其他配置
                 .build();
                 
        OkHttpUtils.initClient(okHttpClient);
```

# 配置持久化 cookie

```
CookieJarImpl cookieJar = new CookieJarImpl(new MemoryCookieStore());
OkHttpClient okHttpClient = new OkHttpClient.Builder()
          .cookieJar(cookieJar)
          //其他配置
         .build();
                 
OkHttpUtils.initClient(okHttpClient);
```

# 配置证书

```
HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(null, null, null);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new LoggerInterceptor("TAG"))
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                .writeTimeout(10000L, TimeUnit.MILLISECONDS)
                .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
                .hostnameVerifier(new HttpsUtils.UnSafeHostnameVerifier())
                .build();
```

# 配置拦截器

```
HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(null, null, null);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new LoggerInterceptor("TAG"))
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                .writeTimeout(10000L, TimeUnit.MILLISECONDS)
                .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
                .hostnameVerifier(new HttpsUtils.UnSafeHostnameVerifier())
                .build();
```

# 自定义callback，重写parseNetworkResponse()方法返回自己需要的类型

```
public abstract class WddTestCallback extends Callback<User>
{
    @Override
    public WddTest parseNetworkResponse(Response response, int id) throws IOException
    {
        String string = response.body().string();
        WddTest wddT = new Gson().fromJson(string, WddTest.class);
        return wddT;
    }
}
```

# 长连接

```
OkHttpUtils
    .getWebSocket()
    .newBuild()
    .execute(new WebSocketListener(){
        //重写的方法
    });
```

# get请求

```
OkHttpUtils
    .get()
    .url(url)
    .id(100)
    .build()
    .execute(new StringCallback());
```

# post请求

```
Map<String, String> m = new HashMap();
m.put("name","wdd");
m.put("pwd","123456");
OkHttpUtils
     .post()
     .url(url)
     .params(m)//如果没有就不传
     .build()
```

# 文件上传

```
List<FileInput> fileList = new ArrayList();
for(i = 0; i < 4; i++){
    fileList.add(new FileInput(key, fileName, file));
}
OkHttpUtils
      .postFile()
      .url(url)
      .mediaType()
      .files(fileList)
      .build()
      .execute(new MyStringCallback());
```

# 文件下载

```
OkHttpUtils
      .get()
      .url(url)
      .build()
      .execute(new FileCallBack("保存路径", "文件名"){
      //重写的方法
      }
```