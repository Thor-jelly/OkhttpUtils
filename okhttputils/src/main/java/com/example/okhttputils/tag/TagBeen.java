package com.example.okhttputils.tag;

/**
 * 类描述：设置tag<br/>
 * 创建人：吴冬冬<br/>
 * 创建时间：2018/5/16 15:41 <br/>
 */
public class TagBeen {
    /**
     * 默认tag
     */
    private static final String DEFAULT_TAG = "default_tag";
    private Object tag = DEFAULT_TAG;
    private boolean isShowDialog = false;

    public Object getTag() {
        return tag;
    }

    public void setTag(Object tag) {
        this.tag = tag;
    }

    public boolean isShowDialog() {
        return isShowDialog;
    }

    public void setShowDialog(boolean showDialog) {
        isShowDialog = showDialog;
    }
}
