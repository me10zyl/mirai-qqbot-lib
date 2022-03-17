package com.yilnz.qqbotlib;

import com.alibaba.fastjson.JSON;
import com.yilnz.qqbotlib.entity.FriendMessage;
import com.yilnz.qqbotlib.entity.QQMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

/**
 * https://github.com/project-mirai/mirai-api-http/blob/master/docs/adapter/HttpAdapter.md
 */
public class TestMain {
    public static void main(String[] args) throws IOException {
        Properties properties = new Properties();
        properties.load(TestMain.class.getClassLoader().getResourceAsStream("app.properties"));
        String property = properties.getProperty("my.qq");
        String property1 = properties.getProperty("my.token");
        System.out.println("QQBot stated:" + property + "," + property1);
        QQBot qqbot = new QQBot(property, property1);
        qqbot.onMessageReceived(new QQMessageListener() {
            @Override
            public void onReceivedFirendMessage(FriendMessage friendMessage) {
                System.out.println("收到消息：" + JSON.toJSONString(friendMessage));
                if(friendMessage.getMessage().equals("你好")){
                    List<QQMessage> qqMessages = new ArrayList<>();
                    qqMessages.add(QQMessage.textMessage("你好鸭！"));
                    qqbot.sendFriendMessage(friendMessage.getSender().getId(), qqMessages);
                }
            }
        });
        new Scanner(System.in).next();
        qqbot.stop();
    }
}
