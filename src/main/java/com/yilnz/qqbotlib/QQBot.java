package com.yilnz.qqbotlib;

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

    public void sendMessage(String targetQQ, List<QQMessage> qqMessageList){
        apiUtil.sendFriendMessage(sessionKey, targetQQ, qqMessageList);
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
            log.info("qqbot start message receiving");
            messageThread.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    int count = apiUtil.countMessage(sessionKey);
                    log.info("qqbot count message {}", count);
                    if(count > 0){
                        String s = apiUtil.fetchMessage(sessionKey);
                        PlainText plainText = new PlainText(s);
                        plainText.nodes().forEach(data->{
                            String type = data.selectJson("type").get();
                            msgJsonHandlerList.forEach(handler->{
                                if(handler.support(type)){
                                    handler.handle(data.get(), listener);
                                }
                            });
                        });
                    }
                }
            }, 0, 1, TimeUnit.SECONDS);
        }
    }
}
