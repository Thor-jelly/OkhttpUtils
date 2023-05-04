# Gson
1. GsonConverterFactory
2. GsonResponseBodyConverter
3. DefaultCallAdapterFactory -> ExecutorCallbackCall
4. HttpMethodService -> invoke() 
5. CallAdapted -> adapt()
6. HttpCall -> enqueue() ->  parseResponse()

# RxJava
1. RxJava3CallAdapterFactory
2. retrofit.create -> loadServiceMethod.invoke -> ServiceMethod.invoke -> HttpServiceMethod.invoke -> RxJava3CallAdapterFactory.adapt -> ResultObservable

# Coroutine
```
@JvmName("awaitNullable")
suspend fun <T : Any> Call<T?>.await(): T? {
    return suspendCancellableCoroutine { continuation ->
        continuation.invokeOnCancellation {
            cancel()
        }
        enqueue(object : Callback<T?> {
            override fun onResponse(call: Call<T?>, response: Response<T?>) {
                if (response.isSuccessful) {
                    continuation.resume(response.body())
                } else {
                    continuation.resumeWithException(HttpException(response))
                }
            }
            override fun onFailure(call: Call<T?>, t: Throwable) {
                continuation.resumeWithException(t)
            }
        })
    }
}

```

# Download

# 单进程监听方案
添加拦截器`ProgressIntercept`

## 多进程下载方案 暂未实现
```
//从响应头 Content-Range 中，取 contentLength
public static long getContentLength(Response response) {
    long contentLength = -1;
    ResponseBody body = response.body();
    if (body != null) {
        if ((contentLength = body.contentLength()) != -1) {
            return contentLength;
        }
    }
    String headerValue = response.header("Content-Range");
    if (headerValue != null) {
        //响应头Content-Range格式 : bytes 100001-20000000/20000001
        try {
            int divideIndex = headerValue.indexOf("/"); //斜杠下标
            int blankIndex = headerValue.indexOf(" ");
            String fromToValue = headerValue.substring(blankIndex + 1, divideIndex);
            String[] split = fromToValue.split("-");
            long start = Long.parseLong(split[0]); //开始下载位置
            long end = Long.parseLong(split[1]);   //结束下载位置
            contentLength = end - start + 1;       //要下载的总长度
        } catch (Exception ignore) {
        }
    }
    return contentLength;
}

    long current = 0;
    BufferedSink sink = Okio.buffer(Okio.sink(ApkFile));
    Buffer buffer = sink.buffer();
    long total = body.contentLength();
    long len;
    int bufferSize = 200 * 1024; //200kb
    BufferedSource source = body.source();
    while ((len = source.read(buffer, bufferSize)) != -1) {
        current += len;
        int progress = ((int) ((current * 100 / total)));
    }
    source.close();
    sink.close();
```