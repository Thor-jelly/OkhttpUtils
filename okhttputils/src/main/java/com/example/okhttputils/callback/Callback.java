package com.example.okhttputils.callback;

import android.support.annotation.MainThread;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;
import android.test.UiThreadTest;
import android.widget.Toast;

import com.example.okhttputils.OkHttpUtils;
import com.example.okhttputils.request.OkHttpRequest;
import com.example.okhttputils.tag.TagBeen;
import com.example.okhttputils.utils.CommentUtils;
import com.example.okhttputils.utils.GetApplication;
import com.example.okhttputils.utils.LoadDialogUtil;

import okhttp3.Call;
import okhttp3.Dispatcher;
import okhttp3.Response;

/**
 * 类描述：回调<br/>
 * 创建人：吴冬冬<br/>
 * 创建时间：2018/5/14 15:41 <br/>
 */
public abstract class Callback<T> {
    private static final String TAG = "123====";

    /**
     * 主线程中
     */
    @MainThread
    public void onError(int code, String errorMessage, int id, OkHttpRequest okHttpRequest){
        if (okHttpRequest.isShowToast) {
            Toast.makeText(GetApplication.get().getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
        }
    };

    /**
     * 主线程中
     */
    @MainThread
    public abstract void onResponse(int code, T response, int id);

    /**
     * 子线程中，解析返回值 我们可以自定义callBack重写该方法进行解析,并返回我们自己需要的值
     */
    @WorkerThread
    public abstract T parseNetworkResponse(Response response, int id, OkHttpRequest okHttpRequest) throws Exception;

    /**
     * 网络请求前 主线程中
     */
    @MainThread
    public void onBefore(int id) {
        Dispatcher dispatcher = OkHttpUtils.getInstance().getOkHttpClient().dispatcher();
        for (Call call : dispatcher.runningCalls()) {
            Object tagTag = call.request().tag();
            if (tagTag instanceof TagBeen) {
                TagBeen tagBeen = (TagBeen) tagTag;
                if (tagBeen.isShowDialog()) {
                    LoadDialogUtil instance = LoadDialogUtil.getInstance();
                    if (instance.isShowing()) {
                        break;
                    }
                    instance.showLoadDialog(CommentUtils.getActivity());
                }
            }
        }

        //下面走自己的逻辑
    }

    /**
     * 网络请求完成 主线程中
     */
    @MainThread
    public void onAfter(int id) {
        Dispatcher dispatcher = OkHttpUtils.getInstance().getOkHttpClient().dispatcher();
        if (dispatcher.runningCallsCount() == 0) {
            LoadDialogUtil instance = LoadDialogUtil.getInstance();
            if (instance.isShowing()) {
                instance.dismissLoadDialog();
            }
        } else {
            boolean isHasShowDialog = false;
            for (Call call : dispatcher.runningCalls()) {
                Object tagTag = call.request().tag();
                if (tagTag instanceof TagBeen) {
                    TagBeen tagBeen = (TagBeen) tagTag;
                    if (tagBeen.isShowDialog()) {
                        LoadDialogUtil instance = LoadDialogUtil.getInstance();
                        if (instance.isShowing()) {
                            isHasShowDialog = true;
                            break;
                        }
                    }
                }
            }

            if (!isHasShowDialog) {
                LoadDialogUtil instance = LoadDialogUtil.getInstance();
                if (instance.isShowing()) {
                    instance.dismissLoadDialog();
                }
            }
        }

        //下面走自己的逻辑


    }

    /**
     * 网络请求进程值 主线程中
     */
    @MainThread
    public void inProgress(float progress, long total, int id) {
    }
}
