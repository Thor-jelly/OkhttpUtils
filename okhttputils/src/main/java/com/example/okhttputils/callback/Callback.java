package com.example.okhttputils.callback;

import android.support.annotation.MainThread;
import android.support.annotation.WorkerThread;
import android.widget.Toast;

import com.example.okhttputils.request.OkHttpRequest;
import com.example.okhttputils.utils.CommontUtils;
import com.example.okhttputils.utils.GetApplication;
import com.example.okhttputils.utils.LoadDialogUtil;

import java.util.Map;

import okhttp3.Response;

/**
 * 类描述：回调<br/>
 * 创建人：吴冬冬<br/>
 * 创建时间：2018/5/14 15:41 <br/>
 */
public abstract class Callback<T> {
    private static final String TAG = "OkHttpUtils";
    public OkHttpRequest mOkHttpRequest;

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
        if (mOkHttpRequest.isShowDialog) {
            LoadDialogUtil instance = LoadDialogUtil.getInstance();
            instance.showLoadDialog(CommontUtils.getActivity());
        }

        /*Dispatcher dispatcher = OkHttpUtils.getInstance().getOkHttpClient().dispatcher();
        int runningCallsCount = dispatcher.runningCallsCount();
        if (runningCallsCount == 0) {
            if (mOkHttpRequest.isShowDialog) {
                LoadDialogUtil instance = LoadDialogUtil.getInstance();
                instance.showLoadDialog(CommontUtils.getActivity());
            }
        } else {
            boolean isShowing = false;
            for (Call call : dispatcher.runningCalls()) {
                if (call.isCanceled()) {
                    continue;
                }
                Object tagTag = call.request().tag();
                if (tagTag instanceof TagBeen) {
                    TagBeen tagBeen = (TagBeen) tagTag;
                    if (tagBeen.isShowDialog()) {
                        LoadDialogUtil instance = LoadDialogUtil.getInstance();
                        if (instance.isShowing()) {
                            isShowing = true;
                            break;
                        }
                    }
                }
            }
            if (!isShowing) {
                if (mOkHttpRequest.isShowDialog) {
                    LoadDialogUtil instance = LoadDialogUtil.getInstance();
                    if (!instance.isShowing()) {
                        instance.showLoadDialog(CommontUtils.getActivity());
                    }
                }
            }
        }*/


        //下面走自己的逻辑
    }

    /**
     * 网络请求完成 主线程中
     */
    @MainThread
    public void onAfter(int id) {
        LoadDialogUtil instance = LoadDialogUtil.getInstance();
        if (instance.isShowing()) {
            instance.dismissLoadDialog();
        }

        /*Dispatcher dispatcher = OkHttpUtils.getInstance().getOkHttpClient().dispatcher();
        int runningCallsCount = dispatcher.runningCallsCount();
        if (runningCallsCount == 0) {
            LoadDialogUtil instance = LoadDialogUtil.getInstance();
            if (instance.isShowing()) {
                instance.dismissLoadDialog();
            }
        } else {
            boolean isHasShowDialog = false;
            for (Call call : dispatcher.runningCalls()) {
                if (call.isCanceled()) {
                    continue;
                }
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
        }*/

        //下面走自己的逻辑
    }

    /**
     * 网络请求进程值 主线程中
     */
    @MainThread
    public void inProgress(float progress, long total, int id) {
    }

    /**
     * 添加动态的公共请求参数
     */
    public Map<String,String> addChangeCommonParameters(){
        return null;
    }
}
