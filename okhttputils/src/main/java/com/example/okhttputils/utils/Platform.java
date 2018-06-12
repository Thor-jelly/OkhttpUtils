package com.example.okhttputils.utils;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.concurrent.Executor;

/**
 * 类描述：切换到主线程,retrofit2 源码切换到主线程<br/>
 * 创建人：吴冬冬<br/>
 * 创建时间：2018/5/16 09:54 <br/>
 */
public class Platform {
    private static final Platform PLATFORM = findPlatform();

    public static Platform get() {
        return PLATFORM;
    }

    private static Platform findPlatform() {
        try {
            Class.forName("android.os.Build");
            if (Build.VERSION.SDK_INT != 0) {
                return new Android();
            }
        } catch (ClassNotFoundException ignored) {
        }
        return new Platform();
    }

    public Executor defaultCallbackExecutor() {
        return null;
    }

    public void execute(Runnable runnable) {
        defaultCallbackExecutor().execute(runnable);
    }


    static class Android extends Platform {
        @Override
        public Executor defaultCallbackExecutor() {
            return new MainThreadExecutor();
        }

        static class MainThreadExecutor implements Executor {
            private final Handler handler = new Handler(Looper.getMainLooper());

            @Override
            public void execute(Runnable r) {
                handler.post(r);
            }
        }
    }
}
