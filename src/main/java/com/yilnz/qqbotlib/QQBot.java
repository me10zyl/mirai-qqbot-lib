package com.yilnz.qqbotlib;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yilnz.qqbotlib.entity.QQMessage;
import com.yilnz.qqbotlib.services.FirendJsonHandler;
import com.yilnz.qqbotlib.services.MsgJsonHandler;
import com.yilnz.qqbotlib.util.ApiUtil;
import com.yilnz.surfing.core.basic.PlainText;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class QQBot {
    private ApiUtil apiUtil;
    private String sessionKey;
    private ScheduledExecutorService messageThread;
    private List<MsgJsonHandler> msgJsonHandlerList;

    public QQBot(String qqNumber, String verifyKey) {
        this("http://localhost:8080", qqNumber, verifyKey);
    }

    public QQBot(String baseUrl, String qqNumber, String verifyKey) {
        this.apiUtil = new ApiUtil(baseUrl);
        this.sessionKey = apiUtil.newSession(verifyKey);
        apiUtil.bind(this.sessionKey, qqNumber);
        msgJsonHandlerList = new ArrayList<>();
        msgJsonHandlerList.add(new FirendJsonHandler());
    }

    public boolean sendMessage(String targetQQ, List<QQMessage> qqMessageList){
        return apiUtil.sendFriendMessage(sessionKey, targetQQ, qqMessageList);
    }

    public void stop(){
        if(messageThread != null){
            messageThread.shutdownNow();
        }
        apiUtil.release(sessionKey);
    }

    public void onMessageReceived(QQMessageListener listener){
        if(messageThread == null) {
            messageThread = Executors.newSingleThreadScheduledExecutor();
            log.debug("qqbot start message receiving");
            messageThread.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    try {
                        int count = apiUtil.countMessage(sessionKey);
//                        log.info("qqbot count message {}", count);
                        if(count > 0){
                            String s = apiUtil.fetchMessage(sessionKey);
                            log.debug("fetched message {}", s);
                            JSONArray array = JSONArray.parseArray(s);
                            array.forEach(data->{
                                String type = ((JSONObject)data).getString("type");
                                log.debug("start handle message type {}", type);
                                msgJsonHandlerList.forEach(handler->{
                                    if(handler.support(type)){
                                        log.debug("handle message {}", s);
                                        handler.handle(data.toString(), listener);
                                    }
                                });
                            });
                        }
                    } catch (Exception e) {
                        log.error("qqbot message cycle error", e);
                    }
                }
            }, 0, 1, TimeUnit.SECONDS);
        }
    }
}
