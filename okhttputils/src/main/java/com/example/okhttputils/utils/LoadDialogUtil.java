package com.example.okhttputils.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;

/**
 * 类描述：无限加载框 <br/>
 * 创建人：吴冬冬<br/>
 * 创建时间：2017/9/7 17:36 <br/>
 */

public class LoadDialogUtil {
    /**
     * 正在加载网络数据...
     */
    public static final String NET = "正在加载网络数据...";
//    /**
//     * 正在更新本地数据...
//     */
//    public static final String LOAD = "正在更新本地数据...";
//    /**
//     * 正在切换模式
//     */
//    public static final String CHANGING_MODEL = "正在切换模式";

    private static LoadDialogUtil mLoadDialogUtil;

    protected ProgressDialog mDialog;
    private Activity mActivity;

    private LoadDialogUtil() {
    }

    public static LoadDialogUtil getInstance() {
        if (mLoadDialogUtil == null) {
            mLoadDialogUtil = new LoadDialogUtil();
        }
        return mLoadDialogUtil;
    }

    public void showLoadDialog(Context context) {
        showLoadDialog(context, 0);
    }

    @SuppressLint("NewApi")
    public void showLoadDialog(Context context, int messageId) {
        if (mDialog != null && mDialog.isShowing() && mActivity != null && !mActivity.isFinishing() && !mActivity.isDestroyed()) {
            mDialog.dismiss();
            mActivity = null;
        }
        mActivity = (Activity) context;
        if (mActivity == null) {
            //CommonUtil.showToast(MyApplication.getContext(), "加载框上下文为空！！！");
            return;
        }
        mDialog = null;
        mDialog = new ProgressDialog(mActivity);
        mDialog.setTitle(null);
        mDialog.setMessage(0 == messageId ? NET : mActivity.getString(messageId));
        mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mDialog.setIndeterminate(false);
//        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setCancelable(false);
//            mDialog.setMax(6);
        if (mActivity == null || mActivity.isFinishing() || mActivity.isDestroyed()) {
            mDialog.dismiss();
            mActivity = null;
            return;
        }
        mDialog.show();
    }

    @SuppressLint("NewApi")
    public void dismissLoadDialog() {
        if (mDialog != null && mDialog.isShowing() && !mActivity.isDestroyed()) {
            if (mDialog != null && mDialog.isShowing() && mActivity != null && !mActivity.isDestroyed()) {
                mDialog.dismiss();
                mDialog = null;
//                CommonUtil.debug("123===", "dimiss");
            }
        }
    }

    public boolean isShowing() {
        if (mDialog != null && mDialog.isShowing()) {
            return true;
        }
        return false;
    }
}
