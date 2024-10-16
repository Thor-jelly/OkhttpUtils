package com.jelly.thor.example.netserver

import com.jelly.thor.example.model.PagerModel
import com.jelly.thor.example.model.RxJavaTestCModel
import com.jelly.thor.example.model.RxJavaTestModel
import com.jelly.thor.okhttputils.OkHttpUtils
import com.jelly.thor.okhttputils.callback.dataConversion
import com.jelly.thor.okhttputils.model.ResponseModel
import io.reactivex.rxjava3.core.Maybe
import okhttp3.Response

/**
 * 创建人：吴冬冬<br/>
 * 创建时间：2023/1/30 15:06 <br/>
 */
object PostApi {
    @JvmStatic
    fun postStringRxJavaTest(): Maybe<RxJavaTestModel> {
        return OkHttpUtils.postString()
            .url(ApiUrlConstant.getApiUrl("test/test"))
            .params(JsonParamsUtils.builder().build())
            .build()
            .rxJava()
            .dataConversion<RxJavaTestModel>()
    }

    @JvmStatic
    fun postStringResponseStr(): Maybe<String> {
        return OkHttpUtils.postString()
            .url(ApiUrlConstant.getApiUrl("test/test"))
            .params(JsonParamsUtils.builder().build())
            .build()
            .rxJava()
            .dataConversion<String>()
    }

    @JvmStatic
    fun postStringResponse(): Maybe<ResponseModel<String>> {
        return OkHttpUtils.postString()
            .url(ApiUrlConstant.getApiUrl("test/test"))
            .params(JsonParamsUtils.builder().build())
            .build()
            .rxJava()
            .dataConversion<ResponseModel<String>>()
    }

    @JvmStatic
    fun postStringOkhttpResponse(): Maybe<Response> {
        return OkHttpUtils.postString()
            .url(ApiUrlConstant.getApiUrl("test/test"))
            .params(JsonParamsUtils.builder().build())
            .build()
            .rxJava()
            .dataConversion<Response>()
    }

    @JvmStatic
    fun postStringList(): Maybe<List<RxJavaTestCModel>> {
        return OkHttpUtils.postString()
            .url(ApiUrlConstant.getApiUrl("test/test"))
            .params(JsonParamsUtils.builder().build())
            .build()
            .rxJava()
            .dataConversion<List<RxJavaTestCModel>>()
    }

    @JvmStatic
    fun postStringPagerResponse(): Maybe<ResponseModel<PagerModel>> {
        return OkHttpUtils.postString()
            .url(ApiUrlConstant.getApiUrl("test/test"))
            .params(JsonParamsUtils.builder().build())
            .build()
            .asRxJava<ResponseModel<PagerModel>>()
    }
}