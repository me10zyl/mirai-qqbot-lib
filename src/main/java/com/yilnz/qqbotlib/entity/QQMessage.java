package com.yilnz.qqbotlib.entity;

import lombok.Data;

@Data
public class QQMessage {
    private String type;
    private String text;
    private String url;

    private QQMessage(){

    }

    public static QQMessage textMessage(String text){
        QQMessage qqMessage = new QQMessage();
        qqMessage.setText(text);
        qqMessage.setType("Plain");
        return qqMessage;
    }


    public static QQMessage imageMessage(String url){
        QQMessage qqMessage = new QQMessage();
        qqMessage.setUrl(url);
        qqMessage.setType("Image");
        return qqMessage;
    }
}
