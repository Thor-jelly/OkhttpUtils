[![GitHub release](https://img.shields.io/badge/release-v1.0.29-green.svg)](https://github.com/Thor-jelly/OkhttpUtils/releases)

```
Add it in your root build.gradle at the end of repositories:

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
Step 2. Add the dependency

	dependencies {
	        implementation 'com.github.Thor-jelly:OkhttpUtils:v1.0.29'
	}
```

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

# 配置baseUrl,如果配置的baseUrl则可以在.url的时候设置baseUrl后面连接的地址段
共同baseUrl可以设置baseURL，如果要在网络的时候设置不同的baseUrl可以用.baseUrl来设置，也可以直接用Url来设置全部的网络连接地址

```
OkHttpUtils.getInstance().setBaseUrl(baseUrl);
```

# 添加静态公共参数

```
OkHttpUtils.getInstance().addCommonParams(commonParams);
```

# 添加动态的公共参数

自定义callback，重写addChangeCommonParameters()方法返回自己需要的动态请求参数

# 添加公共请求头

```
OkHttpUtils.getInstance().addCommonHeaders(commonHeaders);
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

# 自定义callback，重写parseNetworkResponse()方法返回自己需要的类型，如果返回null则不走onResponse方法，并且需要在null的地方执行onError方法

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
