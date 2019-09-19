package com.wuc.lib_network.okhttp.cookie;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
/**
 * @author:     wuchao
 * @date:       2019-09-18 18:13
 * @desciption:
 */
public final class SimpleCookieJar implements CookieJar {
    private final List<Cookie> allCookies = new ArrayList<>();

    @Override
    public synchronized List<Cookie> loadForRequest(HttpUrl url) {
        List<Cookie> result = new ArrayList<>();
        for (Cookie cookie : allCookies) {
            if (cookie.matches(url)) {
                result.add(cookie);
            }
        }
        return result;
    }

    @Override
    public synchronized void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        allCookies.addAll(cookies);
    }
}
