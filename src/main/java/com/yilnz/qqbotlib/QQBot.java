package com.yilnz.qqbotlib;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yilnz.qqbotlib.entity.QQMessage;
import com.yilnz.qqbotlib.listeners.QQEventListener;
import com.yilnz.qqbotlib.services.FirendJsonHandler;
import com.yilnz.qqbotlib.services.NewFriendRequestHandler;
import com.yilnz.qqbotlib.services.MsgJsonHandler;
import com.yilnz.qqbotlib.util.ApiUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class QQBot {
    private ApiUtil apiUtil;
    private ScheduledExecutorService messageThread;
    private List<MsgJsonHandler> msgJsonHandlerList;

    public QQBot(String qqNumber, String verifyKey) {
        this("http://localhost:8080", qqNumber, verifyKey);
    }

    public QQBot(String baseUrl, String qqNumber, String verifyKey) {
        this.apiUtil = new ApiUtil(baseUrl, verifyKey, qqNumber);
        msgJsonHandlerList = new ArrayList<>();
        msgJsonHandlerList.add(new FirendJsonHandler());
        msgJsonHandlerList.add(new NewFriendRequestHandler());
    }

    public boolean sendFriendMessage(String targetQQ, List<QQMessage> qqMessageList){
        return apiUtil.sendFriendMessage(targetQQ, qqMessageList);
    }

    public boolean sendGroupMessage(String targetQQ, List<QQMessage> qqMessageList){
        return apiUtil.sendGroupMessage(targetQQ, qqMessageList);
    }

    public void stop(){
        if(messageThread != null){
            messageThread.shutdownNow();
        }
        apiUtil.release();
    }

    public void onMessageReceived(QQEventListener listener){
        if(messageThread == null) {
            messageThread = Executors.newSingleThreadScheduledExecutor();
            log.debug("qqbot start message receiving");
            messageThread.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    try {
                        int count = apiUtil.countMessage();
//                        log.info("qqbot count message {}", count);
                        if(count > 0){
                            String s = apiUtil.fetchMessage();
                            log.debug("fetched message {}", s);
                            JSONArray array = JSONArray.parseArray(s);
                            array.forEach(data->{
                                String type = ((JSONObject)data).getString("type");
                                log.debug("start handle message type {}", type);
                                msgJsonHandlerList.forEach(handler->{
                                    if(handler.support(type)){
                                        log.debug("handle message {}", data.toString());
                                        handler.handle(data.toString(), listener, apiUtil);
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
