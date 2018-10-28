package com.jelly.thor.okhttputils.utils;

/**
 * 类描述：错误码 <br/>
 * 创建人：吴冬冬<br/>
 * 创建时间：2018/5/14 17:21 <br/>
 */
public class ErrorCode {
    /**
     * 异常
     */
    public static final int RESPONSE_ERROR = -10009;
    /**
     * response.body().string() == null
     */
    public static final int RESPONSE_NULL = -10010;
    /**
     * 数据解析异常
     */
    public static final int RESPONSE_OBJ = -10011;
    /**
     * 网络异常
     */
    public static final int RESPONSE_NET = -10012;
    /**
     * 网络被取消
     */
    public static final int RESPONSE_NET_CANCEL = -10013;
    /**
     * 添加参数异常
     */
    public static final int  REQUEST_PARAMS_EXCEPTION = -10014;
}
