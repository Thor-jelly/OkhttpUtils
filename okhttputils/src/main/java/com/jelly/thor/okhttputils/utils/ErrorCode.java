package com.jelly.thor.okhttputils.utils;

/**
 * 类描述：错误码 <br/>
 * 创建人：吴冬冬<br/>
 * 创建时间：2018/5/14 17:21 <br/>
 */
public class ErrorCode {
    /**
     * 未知错误
     */
    public static final int UNKNOWN = -10010;
    /**
     * 数据解析异常
     */
    public static final int PARSE_ERROR = -10011;
    /**
     * 网络异常
     */
    public static final int NET_ERROR = -10012;
    /**
     * 网络被取消
     */
    public static final int NET_CANCEL = -10013;
    /**
     * 添加参数异常
     */
    public static final int PARAMS_EXCEPTION = -10014;
//    /**
//     * 协议出错
//     */
//    public static final int HTTP_ERROR = -10015;

    /**
     * 证书出错
     */
    public static final int SSL_ERROR = -10016;

    /**
     * 连接超时
     */
    public static final int TIMEOUT_ERROR = -10017;

    /**
     * 自己处理成功返回 失败
     */
    public static final int HANDLE_SUCCESS_ERROR = -10018;

    /**
     * 保存文件失败
     */
    public static final int SAVE_FILE = -10019;
}
