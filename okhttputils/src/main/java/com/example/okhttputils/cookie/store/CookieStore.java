package com.example.okhttputils.cookie.store;

import java.util.List;

import okhttp3.Cookie;
import okhttp3.HttpUrl;

/**
 * 类描述：cookie 通用方法接口<br/>
 * 创建人：吴冬冬<br/>
 * 创建时间：2018/5/15 10:59 <br/>
 */
public interface CookieStore {

    void add(HttpUrl uri, List<Cookie> cookie);

    List<Cookie> get(HttpUrl uri);

    List<Cookie> getCookies();

    boolean remove(HttpUrl uri, Cookie cookie);

    boolean removeAll();

}
