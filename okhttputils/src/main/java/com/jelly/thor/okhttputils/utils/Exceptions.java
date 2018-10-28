package com.jelly.thor.okhttputils.utils;

/**
 * 类描述：自定义异常<br/>
 * 创建人：吴冬冬<br/>
 * 创建时间：2018/5/14 15:13 <br/>
 */
public class Exceptions {
    public static void illegalArgument(String msg, Object... params) {
        throw new IllegalArgumentException(String.format(msg, params));
    }

    public static void exception(String msg, Object... params) throws Exception {
        throw new Exception(String.format(msg, params));
    }
}
