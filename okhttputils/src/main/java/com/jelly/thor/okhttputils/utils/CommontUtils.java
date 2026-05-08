package com.jelly.thor.okhttputils.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Map;

/**
 * 类描述：常用工具类<br/>
 * 创建人：吴冬冬<br/>
 * 创建时间：2018/5/15 10:37 <br/>
 */
public class CommontUtils {
    /**
     * 判断当前是否有网络
     */
    public static boolean networkAvailable() {
        Context context = GetApplication.get().getApplicationContext();
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        }
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Network network = connectivityManager.getActiveNetwork();
                if (network == null) {
                    return false;
                }
                NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
                if (capabilities == null) {
                    return false;
                }
                return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                        && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
            } else {
                @SuppressLint("MissingPermission")
                NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                return activeNetworkInfo != null && activeNetworkInfo.isConnected();
            }
        } catch (Exception e) {
            // 权限不足或其他异常时返回false
            return false;
        }
    }

    /**
     * 获取文件名MimeType
     */
    public static String guessMimeType(String path) {
        if (path == null || path.isEmpty()) {
            return "image/png";
        }
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        try {
            String contentType = fileNameMap.getContentTypeFor(URLEncoder.encode(path, "UTF-8"));
            return contentType != null ? contentType : "image/png";
        } catch (UnsupportedEncodingException e) {
            // UTF-8 应该总是支持的，但为了安全起见
            return "image/png";
        }
    }

    /**
     * 获取当前Activity（使用反射，性能较差，建议谨慎使用）
     * @deprecated 此方法使用反射，性能较差，且在不同Android版本可能失效，建议使用其他方式获取Activity
     */
    @Deprecated
    public static Activity getActivity() {
        try {
            Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
            Object activityThread = activityThreadClass.getMethod("currentActivityThread").invoke(null);
            Field activitiesField = activityThreadClass.getDeclaredField("mActivities");
            activitiesField.setAccessible(true);
            @SuppressWarnings("unchecked")
            Map<Object, Object> activities = (Map<Object, Object>) activitiesField.get(activityThread);
            if (activities == null) {
                return null;
            }
            for (Object activityRecord : activities.values()) {
                Class<?> activityRecordClass = activityRecord.getClass();
                Field pausedField = activityRecordClass.getDeclaredField("paused");
                pausedField.setAccessible(true);
                if (!pausedField.getBoolean(activityRecord)) {
                    Field activityField = activityRecordClass.getDeclaredField("activity");
                    activityField.setAccessible(true);
                    return (Activity) activityField.get(activityRecord);
                }
            }
        } catch (Exception e) {
            // 反射失败，返回null
        }
        return null;
    }


    /**
     * 解析一个类上面的class信息
     */
    public static Class<?> analysisClazzInfo(Object object) {
        Type genType = object.getClass().getGenericSuperclass();
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        return (Class<?>) params[0];
    }
}
