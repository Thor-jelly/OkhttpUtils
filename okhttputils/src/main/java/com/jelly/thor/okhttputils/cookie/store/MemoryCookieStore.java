package com.jelly.thor.okhttputils.cookie.store;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.HttpUrl;

/**
 * 类描述：内存保存cookie<br/>
 * 创建人：吴冬冬<br/>
 * 创建时间：2018/5/15 10:59 <br/>
 */
public class MemoryCookieStore implements CookieStore {
    private final HashMap<String, List<Cookie>> allCookies = new HashMap<>();

    /**
     * 将url的所有Cookie保存
     */
    @Override
    public void saveCookie(HttpUrl url, List<Cookie> cookie) {
        for (Cookie itemCookie : cookie) {
            saveCookie(url, itemCookie);
        }
    }

    @Override
    public void saveCookie(HttpUrl url, Cookie cookie) {
        if (!allCookies.containsKey(url.host())) {
            allCookies.put(url.host(), new ArrayList<>());
        }
        //当前cookie是否过期
        if (isCookieExpired(cookie)) {
            removeCookie(url, cookie);
        } else {
            List<Cookie> oldCookies = allCookies.get(url.host());
            Iterator<Cookie> itOld = oldCookies.iterator();
            while (itOld.hasNext()) {
                String v = itOld.next().name();
                if (cookie.name().equals(v)) {
                    itOld.remove();
                }
            }
            oldCookies.add(cookie);
        }
    }

    /**
     * 根据当前url获取所有需要的cookie,只返回没有过期的cookie
     */
    @Override
    public List<Cookie> loadCookie(HttpUrl url) {
        List<Cookie> ret = new ArrayList<>();
        if (!allCookies.containsKey(url.host())) return ret;

        Collection<Cookie> urlCookies = allCookies.get(url.host());
        for (Cookie cookie : urlCookies) {
            if (isCookieExpired(cookie)) {
                removeCookie(url, cookie);
            } else {
                ret.add(cookie);
            }
        }
        return ret;
    }

    /**
     * 获取所有的cookie
     */
    @Override
    public List<Cookie> getAllCookie() {
        List<Cookie> ret = new ArrayList<>();
        for (String key : allCookies.keySet()) {
            ret.addAll(allCookies.get(key));
        }
        return ret;
    }

    @Override
    public List<Cookie> getCookie(HttpUrl url) {
        List<Cookie> ret = new ArrayList<>();
        if (!allCookies.isEmpty() && null != allCookies.get(url.host())) {
            ret.addAll(allCookies.get(url.host()));
        }
        return ret;
    }

    @Override
    public boolean removeCookie(HttpUrl url, Cookie cookie) {
        List<Cookie> cookies = allCookies.get(url.host());
        if (cookies != null) {
            return cookies.remove(cookie);
        }
        return false;
    }

    @Override
    public boolean removeCookie(HttpUrl url) {
        List<Cookie> cookies = allCookies.get(url.host());
        if (cookies != null) {
            cookies.clear();
            return true;
        }
        return false;
    }

    @Override
    public boolean removeAllCookie() {
        allCookies.clear();
        return true;
    }

    /**
     * 当前cookie是否过期
     */
    private static boolean isCookieExpired(Cookie cookie) {
        return cookie.expiresAt() < System.currentTimeMillis();
    }
}
