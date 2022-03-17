package com.yilnz.qqbotlib.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yilnz.qqbotlib.entity.QQFriend;
import com.yilnz.qqbotlib.exception.MiraiError;
import com.yilnz.surfing.core.SurfHttpRequest;
import com.yilnz.surfing.core.SurfHttpRequestBuilder;
import com.yilnz.surfing.core.SurfSpider;
import com.yilnz.surfing.core.basic.Html;
import com.yilnz.surfing.core.basic.Page;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

@Slf4j
public class ApiUtil {

    private String baseUrl;

    public ApiUtil(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String newSession(String verifyKey){
        SurfHttpRequest r = new SurfHttpRequestBuilder(baseUrl, "POST").build();
        r.setBody("{\"verifyKey\": \"" + verifyKey + "\"}");
        Page page = SurfSpider.create().addRequest(r).request().get(0);
        Integer code = page.getHtml().selectJson("$.code").getInt();
        if (code != 0) {
            String s = "创建session失败：" + page.getHtml().selectJson("$.message").get();
            log.error(s);
            throw new MiraiError(s);
        }
        return page.getHtml().selectJson("$.session").get();
    }

    public void bind(String sessionKey, String qq){
        SurfHttpRequest r = new SurfHttpRequestBuilder(baseUrl + "/bind", "POST").build();
        r.setBody("{\"sessionKey\": \"" + sessionKey + "\",\"qq\" : \""+ qq +"\"}");
        Page page = SurfSpider.create().addRequest(r).request().get(0);
        Integer code = page.getHtml().selectJson("$.code").getInt();
        if (code != 0) {
            String s = "绑定session到qq号失败：" + page.getHtml().selectJson("$.message").get();
            log.error(s);
            throw new MiraiError(s);
        }
    }

    public List<QQFriend> getFriendList(String sessionKey){
        return doGetList(sessionKey, "/friendList", "获取朋友列表失败", QQFriend.class);
    }

    public int countMessage(String sessionKey){
        return Integer.parseInt(doGet(sessionKey, "/countMessage", "countMessage失败"));
    }

    public String fetchMessage(String sessionKey){
        return doGet(sessionKey, "/fetchMessage", "fetchMessage失败");
    }

    private String doGet(String sessionKey, String url, String errorMsg){
        SurfHttpRequest r = new SurfHttpRequestBuilder(baseUrl + url, "GET").build();
        r.addHeader("sessionKey", sessionKey);
        Page page = SurfSpider.create().addRequest(r).request().get(0);
        Integer code = page.getHtml().selectJson("$.code").getInt();
        if (code != 0) {
            String s = errorMsg + "：" + page.getHtml().selectJson("$.message").get();
            log.error(s);
            throw new MiraiError(s);
        }
        return page.getHtml().selectJson("$.data").get();
    }

    private <T> List<T> doGetList(String sessionKey, String url, String errorMsg, Class<T> tClass){
        SurfHttpRequest r = new SurfHttpRequestBuilder(baseUrl + url, "GET").build();
        r.addHeader("sessionKey", sessionKey);
        Page page = SurfSpider.create().addRequest(r).request().get(0);
        Integer code = page.getHtml().selectJson("$.code").getInt();
        if (code != 0) {
            String s = errorMsg + "：" + page.getHtml().selectJson("$.message").get();
            log.error(s);
            throw new MiraiError(s);
        }
        return JSONArray.parseArray(page.getHtml().selectJson("$.data").get(),  tClass);
    }
}
