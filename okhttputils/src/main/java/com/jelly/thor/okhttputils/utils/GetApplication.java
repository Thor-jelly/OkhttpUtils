package com.jelly.thor.okhttputils.utils;

import android.annotation.SuppressLint;
import android.app.Application;

import java.lang.reflect.Method;

/**
 * 类描述：通过反射获取Application上下文<br/>
 * 创建人：吴冬冬<br/>
 * 创建时间：2018/5/15 09:52 <br/>
 */
public class GetApplication {

    @SuppressLint("StaticFieldLeak")
    private static Application application;

    public static Application get() {
        if (application == null) {
            synchronized (GetApplication.class) {
                if (application == null) {
                    new GetApplication();
                }
            }
        }
        return application;
    }

    @SuppressWarnings("all")
    private GetApplication() {
        Object activityThread;
        try {
            Class acThreadClass = Class.forName("android.app.ActivityThread");
            if (acThreadClass == null)
                return;
            Method acThreadMethod = acThreadClass.getMethod("currentActivityThread");
            if (acThreadMethod == null) {
                return;
            }
            acThreadMethod.setAccessible(true);
            activityThread = acThreadMethod.invoke(null);
            Method applicationMethod = activityThread.getClass().getMethod("getApplication");
            if (applicationMethod == null) {
                return;
            }
            Object app = applicationMethod.invoke(activityThread);
            application = (Application) app;
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
