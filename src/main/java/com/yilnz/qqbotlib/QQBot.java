package com.yilnz.qqbotlib;

import com.yilnz.qqbotlib.util.ApiUtil;
import com.yilnz.surfing.core.SurfHttpRequest;
import com.yilnz.surfing.core.SurfHttpRequestBuilder;
import com.yilnz.surfing.core.SurfSpider;
import com.yilnz.surfing.core.basic.Page;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class QQBot {
    private ApiUtil apiUtil;
    private String sessionKey;
    private ScheduledExecutorService messageThread;

    public QQBot(String qqNumber, String verifyKey) {
        this("http://localhost:8080", qqNumber, verifyKey);
    }

    public QQBot(String baseUrl, String qqNumber, String verifyKey) {
        this.apiUtil = new ApiUtil(baseUrl);
        this.sessionKey = apiUtil.newSession(verifyKey);
        apiUtil.bind(this.sessionKey, qqNumber);
    }

    public void onMessageReceived(QQMessageListener listener){
        if(messageThread == null) {
            messageThread = Executors.newSingleThreadScheduledExecutor();
            messageThread.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    int count = apiUtil.countMessage(sessionKey);
                    if(count > 0){
                        String s = apiUtil.fetchMessage(sessionKey);

                    }
                }
            }, 0, 1, TimeUnit.SECONDS);
        }
    }
}
