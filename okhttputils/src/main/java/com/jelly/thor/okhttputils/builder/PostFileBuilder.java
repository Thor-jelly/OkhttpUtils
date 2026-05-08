package com.jelly.thor.okhttputils.builder;

import android.webkit.URLUtil;

import androidx.annotation.NonNull;

import com.jelly.thor.okhttputils.OkHttpUtils;
import com.jelly.thor.okhttputils.request.PostFileRequest;
import com.jelly.thor.okhttputils.request.RequestCall;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * 类描述：文件上传<br/>
 * 创建人：吴冬冬<br/>
 * 创建时间：2018/5/15 15:17 <br/>
 */
public class PostFileBuilder extends OkHttpRequestBuilder<PostFileBuilder> implements HasParameters<PostFileBuilder>, HasHeaders<PostFileBuilder> {
    private List<FileInput> file;

    public PostFileBuilder files(@NonNull List<FileInput> fileInputList) {
        this.file = fileInputList;
        return this;
    }

    @Override
    public PostFileBuilder params(@NonNull Map<String, String> params) {
        initParamsIfNeeded().putAll(params);
        return this;
    }

    @Override
    public PostFileBuilder addParam(@NonNull String key, @NonNull String value) {
        initParamsIfNeeded().put(key, value);
        return this;
    }

    @Override
    public PostFileBuilder headers(@NonNull Map<String, String> headers) {
        initHeadersIfNeeded().putAll(headers);
        return this;
    }

    @Override
    public PostFileBuilder addHeader(@NonNull String key, @NonNull String value) {
        initHeadersIfNeeded().put(key, value);
        return this;
    }

    @Override
    public RequestCall build() {
        String myUrl = getNewUrl();
        return new PostFileRequest(myUrl, file, this).build();
    }

    public static class FileInput {
        public String key;
        public String filename;
        public File file;

        public FileInput(String name, String filename, File file) {
            this.key = name;
            this.filename = filename;
            this.file = file;
        }

        @Override
        public String toString() {
            return "FileInput{" +
                    "key='" + key + '\'' +
                    ", filename='" + filename + '\'' +
                    ", file=" + file +
                    '}';
        }
    }
}
