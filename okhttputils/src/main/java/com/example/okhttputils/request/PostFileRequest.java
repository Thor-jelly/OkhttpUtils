package com.example.okhttputils.request;

import com.example.okhttputils.OkHttpUtils;
import com.example.okhttputils.builder.PostFileBuilder;
import com.example.okhttputils.utils.CommentUtils;
import com.example.okhttputils.utils.Exceptions;

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

    public PostFileRequest(String url, Object tag, Map<String, String> params, Map<String, String> headers, int id, List<PostFileBuilder.FileInput> file, boolean isShowDialog, boolean isShowToast) {
        super(url, tag, params, headers, id, isShowDialog, isShowToast);
        this.file = file;
        if (this.file == null || this.file.size() < 1) {
            Exceptions.illegalArgument("the file can not be null !");
        }
    }

    @Override
    protected RequestBody requestBody() {
        MultipartBody body = moreFile();
        return body;
    }

    /**
     * 多文件上传或者有参数文件上传
     *
     * @return
     */
    private MultipartBody moreFile() {
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);

        if (params != null && !params.isEmpty()) {
            for (String key : params.keySet()) {
                /*builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + key + "\""),
                        RequestBody.create(null, params.get(key)));*/
                builder.addFormDataPart(key, params.get(key));
            }
        }

        Map<String, String> commonParams = OkHttpUtils.getInstance().getCommonParams();
        if (commonParams != null && !commonParams.isEmpty()) {
            for (String key : commonParams.keySet()) {
                builder.addFormDataPart(key, commonParams.get(key));
            }
        }

        for (int i = 0; file != null && i < file.size(); i++) {
            PostFileBuilder.FileInput fileInput = file.get(i);
            RequestBody fileBody = RequestBody.create(MediaType.parse(CommentUtils.guessMimeType(fileInput.filename)), fileInput.file);
            builder.addFormDataPart(fileInput.key, fileInput.filename, fileBody);
        }
        return builder.build();
    }
}
