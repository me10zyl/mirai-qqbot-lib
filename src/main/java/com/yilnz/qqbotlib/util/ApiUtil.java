package com.yilnz.qqbotlib.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yilnz.qqbotlib.entity.NewFriendRequestHandleResult;
import com.yilnz.qqbotlib.entity.QQFriend;
import com.yilnz.qqbotlib.entity.QQMessage;
import com.yilnz.qqbotlib.exception.MiraiError;
import com.yilnz.qqbotlib.httpjson.NewFriendRequestEventJson;
import com.yilnz.surfing.core.SurfHttpRequest;
import com.yilnz.surfing.core.SurfHttpRequestBuilder;
import com.yilnz.surfing.core.SurfSpider;
import com.yilnz.surfing.core.basic.Page;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class ApiUtil {

    private String baseUrl;
    private String verifyKey;
    private String qq;
    private String sessionKey;

    public ApiUtil(String baseUrl, String verifyKey, String qq) {
        this.baseUrl = baseUrl;
        this.verifyKey = verifyKey;
        this.qq = qq;
        init();
    }

    public void init(){
        this.sessionKey = newSession();
        bind();
    }

    public String newSession(){
        SurfHttpRequest r = new SurfHttpRequestBuilder(baseUrl + "/verify", "POST").build();
        r.setBody("{\"verifyKey\": \"" + verifyKey + "\"}");
        Page page = SurfSpider.create().addRequest(r).request().get(0);
        log.info("newSession:" + page.getStatusCode());
        Integer code = page.getHtml().selectJson("$.code").getInt();
        if (code != 0) {
            String s = "创建session失败：" + page.getHtml().selectJson("$.message").get();
            log.error(s);
            throw new MiraiError(s);
        }
        return page.getHtml().selectJson("$.session").get();
    }

    public void bind(){
        SurfHttpRequest r = new SurfHttpRequestBuilder(baseUrl + "/bind", "POST").build();
        r.setBody("{\"sessionKey\": \"" + sessionKey + "\",\"qq\" : \""+ qq +"\"}");
        Page page = SurfSpider.create().addRequest(r).request().get(0);
        log.info("bind:" + page.getStatusCode());
        Integer code = page.getHtml().selectJson("$.code").getInt();
        if (code != 0) {
            String s = "绑定session到qq号失败：" + page.getHtml().selectJson("$.message").get();
            log.error(s);
            throw new MiraiError(s);
        }
    }

    public List<QQFriend> getFriendList(){
        return doGetList( "/friendList", "获取朋友列表失败", QQFriend.class, new AtomicInteger(0));
    }

    public boolean sendFriendMessage(String targetQQ, List<QQMessage> qqMessages){
        List<QQFriend> friendList = getFriendList();
        if(friendList.stream().anyMatch(e->String.valueOf(e.getId()).equals(targetQQ))){
            return doPost("/sendFriendMessage",  String.format( "{\n" +
                    "  \"target\":%s,\n" +
                    "  \"messageChain\": %s\n}", targetQQ, JSONArray.toJSONString(qqMessages)), new AtomicInteger(0));
        }
        log.debug("不是好友，不发消息:" + targetQQ + "," + qqMessages);
        return false;
    }

    public boolean sendGroupMessage( String targetQQ, List<QQMessage> qqMessages){
        return doPost("/sendGroupMessage",  String.format( "{\n" +
                "  \"target\":%s,\n" +
                "  \"messageChain\": %s\n}", targetQQ, JSONArray.toJSONString(qqMessages)), new AtomicInteger(0));
    }

    public int countMessage(){
        return Integer.parseInt(doGet("/countMessage", "countMessage失败", new AtomicInteger(0)));
    }

    public String fetchMessage(){
        return doGet("/fetchMessage", "fetchMessage失败", new AtomicInteger(0));
    }

    public void release() {
        doPost("/release", null, new AtomicInteger(0));
    }

    public void handleNewFriendRequestEvent(NewFriendRequestEventJson json){
        if(json.getMessage() == null){
            json.setMessage("");
        }
        doPost("/resp/newFriendRequestEvent", JSON.toJSONString(json), new AtomicInteger(0));
    }


    // base methods

    public boolean doPost(String url, String body, AtomicInteger integer){
        log.debug("sendPost:" + url + ",body=" + body);
        SurfHttpRequest r = new SurfHttpRequestBuilder(baseUrl + url, "POST").build();
        r.addHeader("sessionKey", sessionKey);
        r.addHeader("Content-Type" , "application/json");
        r.setBody(body);
        Page page = SurfSpider.create().addRequest(r).request().get(0);
        Integer code = page.getHtml().selectJson("$.code").getInt();
        if(page.getStatusCode() != 200){
            throw new MiraiError("doPost with url " + url + " return statusCode:" +page.getStatusCode());
        }

        boolean success = code == 0;
        if(!success){
            log.error("do post error:" + url + ":" + page.getHtml());
            if(integer.incrementAndGet() == 0 && (code == 3 || code == 4)){
                init();
                return doPost(url, body, integer);
            }
        }
        return success;
    }

    private String doGet(String url, String errorMsg, AtomicInteger integer){
        SurfHttpRequest r = new SurfHttpRequestBuilder(baseUrl + url, "GET").build();
        r.addHeader("sessionKey", sessionKey);
        Page page = SurfSpider.create().addRequest(r).request().get(0);
        Integer code = page.getHtml().selectJson("$.code").getInt();
        if (code != 0) {
            String s = errorMsg + "：" + page.getHtml().selectJson("$.message").get();
            log.error(s);
            if(integer.incrementAndGet() == 0 && (code == 3 || code == 4)){
                init();
                return doGet(url, errorMsg, integer);
            }
            throw new MiraiError(s);
        }
        return ((JSONObject)JSON.parse(page.getHtml().get())).getString("data");
    }

    private <T> List<T> doGetList( String url, String errorMsg, Class<T> tClass, AtomicInteger integer){
        SurfHttpRequest r = new SurfHttpRequestBuilder(baseUrl + url, "GET").build();
        r.addHeader("sessionKey", sessionKey);
        Page page = SurfSpider.create().addRequest(r).request().get(0);
        Integer code = page.getHtml().selectJson("$.code").getInt();
        if (code != 0) {
            String s = errorMsg + "：" + page.getHtml().selectJson("$.message").get();
            log.error(s);
            if(integer.incrementAndGet() == 0 && (code == 3 || code == 4)){
                init();
                return doGetList(url, errorMsg, tClass, integer);
            }
            throw new MiraiError(s);
        }
        String text = page.getHtml().selectJson("$.data").get();
         try {
             return JSONArray.parseArray(text, tClass);
         }catch (Exception e){
             return JSONArray.parseArray("[" + text + "]", tClass);
         }

    }


}
