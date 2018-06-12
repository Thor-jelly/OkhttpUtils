package com.example.okhttputils.builder;

import com.example.okhttputils.request.PostFileRequest;
import com.example.okhttputils.request.RequestCall;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;

/**
 * 类描述：文件上传<br/>
 * 创建人：吴冬冬<br/>
 * 创建时间：2018/5/15 15:17 <br/>
 */
public class PostFileBuilder extends OkHttpRequestBuilder<PostFileBuilder>{
    private List<FileInput> file;

    public OkHttpRequestBuilder files(List<FileInput> fileInputList) {
        this.file = fileInputList;
        return this;
    }

    @Override
    public RequestCall build() {
        return new PostFileRequest(url, tag, params, headers, id, file, isShowDialog, isShowToast).build();
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
