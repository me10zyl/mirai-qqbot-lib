package com.yilnz.qqbotlib;

import com.yilnz.qqbotlib.entity.FriendMessage;
import com.yilnz.qqbotlib.entity.QQMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

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
                if(friendMessage.getMessage().equals("你好")){
                    List<QQMessage> qqMessages = new ArrayList<>();
                    qqMessages.add(QQMessage.textMessage("你好鸭！"));
                    qqbot.sendMessage(friendMessage.getSender().getId(), qqMessages);
                }
            }
        });
        new Scanner(System.in).nextLine();
    }
}
