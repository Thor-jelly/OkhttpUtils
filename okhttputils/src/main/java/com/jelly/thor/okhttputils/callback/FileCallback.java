package com.jelly.thor.okhttputils.callback;

import android.net.Uri;

import com.jelly.thor.okhttputils.request.OkHttpRequest;
import com.jelly.thor.okhttputils.utils.GetApplication;
import com.jelly.thor.okhttputils.utils.Platform;
import com.jelly.thor.okhttputils.utils.file.FileEKt;
import com.jelly.thor.okhttputils.utils.file.FileInProgress;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.Response;

/**
 * 类描述：下载返回调用的 <br/>
 * 创建人：吴冬冬<br/>
 * 创建时间：2018/5/15 18:22 <br/>
 */
public abstract class FileCallback extends Callback<Uri> {
    /**
     * 目标文件存储的文件夹路径
     */
    private String destFileDir;
    /**
     * 目标文件存储的文件名
     */
    private String destFileName;

    public FileCallback(String destFileName) {
        this.destFileDir = "ddw/";
        this.destFileName = destFileName;
    }

    /**
     * @param destFileDir ddw/ 格式
     */
    public FileCallback(String destFileDir, String destFileName) {
        this.destFileDir = destFileDir;
        this.destFileName = destFileName;
    }

    @Override
    public Uri parseNetworkResponse(Response response, int id, OkHttpRequest okHttpRequest) throws Exception {
        return saveFile(response, id);
    }

    public Uri saveFile(Response response, final int id) throws IOException {
//        InputStream is = null;
//        byte[] buf = new byte[2048];
//        int len = 0;
//        FileOutputStream fos = null;
        try {
            InputStream is = response.body().byteStream();
            long total = response.body().contentLength();

            return FileEKt.save2File(is, GetApplication.get(), destFileName, destFileDir, new FileInProgress(total) {
                @Override
                public void inProgress(float progress, long total) {
                    //返回下载的进度
                    Platform.get().execute(new Runnable() {
                        @Override
                        public void run() {
                            FileCallback.this.inProgress(progress, total, id);
                        }
                    });
                }
            });
//            long sum = 0;
//
//            File dir = new File(destFileDir);
//            if (!dir.exists()) {
//                dir.mkdirs();
//            }
//            File file = new File(dir, destFileName);
//            fos = new FileOutputStream(file);
//            while ((len = is.read(buf)) != -1) {
//                sum += len;
//                fos.write(buf, 0, len);
//                final long finalSum = sum;
//
//                //返回下载的进度
//                Platform.get().execute(new Runnable() {
//                    @Override
//                    public void run() {
//                        inProgress(finalSum * 1.0f / total, total, id);
//                    }
//                });
//            }
//            fos.flush();
//            return file;
        } finally {
            try {
                response.body().close();
                //FileEKt.save2File 中有 close方法
                //if (is != null) is.close();
            } catch (Exception e) {
            }
//            try {
//                if (fos != null) fos.close();
//            } catch (IOException e) {
//            }
        }
    }
}
