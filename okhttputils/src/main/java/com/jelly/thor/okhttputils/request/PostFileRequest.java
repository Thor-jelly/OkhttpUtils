package com.jelly.thor.okhttputils.request;

import com.jelly.thor.okhttputils.OkHttpUtils;
import com.jelly.thor.okhttputils.builder.OkHttpRequestBuilder;
import com.jelly.thor.okhttputils.builder.PostFileBuilder;
import com.jelly.thor.okhttputils.utils.CommontUtils;
import com.jelly.thor.okhttputils.utils.Exceptions;

import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * 类描述：文件上传<br/>
 * 创建人：吴冬冬<br/>
 * 创建时间：2018/5/15 15:19 <br/>
 */
public class PostFileRequest extends OkHttpRequest {
    private List<PostFileBuilder.FileInput> file;

    public PostFileRequest(String url, List<PostFileBuilder.FileInput> file, OkHttpRequestBuilder<PostFileBuilder> okHttpRequestBuilder) {
        super(url, okHttpRequestBuilder);
        this.file = file;
        if (this.file == null || this.file.size() < 1) {
            Exceptions.illegalArgument("the file can not be null !");
        }
    }

    @Override
    protected RequestBody requestBody() {
        return moreFile();
    }

    /**
     * 多文件上传或者有参数文件上传
     */
    private MultipartBody moreFile() {
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);

        addRequestParams(builder, okHttpRequestBuilder.getParams());

        Map<String, String> commonParams = OkHttpUtils.getInstance().getCommonParams();
        addRequestParams(builder, commonParams);

        for (int i = 0; file != null && i < file.size(); i++) {
            PostFileBuilder.FileInput fileInput = file.get(i);
            RequestBody fileBody = RequestBody.create(MediaType.parse(CommontUtils.guessMimeType(fileInput.filename)), fileInput.file);
            builder.addFormDataPart(fileInput.key, fileInput.filename, fileBody);
        }
        return builder.build();
    }

    /**
     * 添加请求参数
     */
    private void addRequestParams(MultipartBody.Builder formBody, Map<String, String> params) {
        if (params != null && !params.isEmpty()) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (value == null) {
                    throw new IllegalArgumentException("参数中的" + key + " 赋值为null");
                }
                formBody.addFormDataPart(key, value);
            }
        }
    }
}
