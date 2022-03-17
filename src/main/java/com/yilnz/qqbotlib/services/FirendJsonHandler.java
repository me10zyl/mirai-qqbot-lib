package com.yilnz.qqbotlib.services;

import com.alibaba.fastjson.JSONObject;
import com.yilnz.qqbotlib.QQMessageListener;
import com.yilnz.qqbotlib.entity.FriendMessage;
import com.yilnz.qqbotlib.entity.Sender;
import com.yilnz.surfing.core.basic.Json;

import java.util.Date;
public class FirendJsonHandler implements MsgJsonHandler {
    @Override
    public boolean support(String type) {
        return "FriendMessage".equals(type);
    }

    @Override
    public void handle(String singleText, QQMessageListener listener) {
        FriendMessage friendMessage = new FriendMessage();
        Json json = new Json(singleText);
        json.selectJson("$.messageChain").nodes().forEach(d->{
            String type = d.selectJson("type").get();
            if("Source".equals(type)){
                friendMessage.setTime(new Date(Long.parseLong(d.selectJson("$.time").get())*1000));
            }
            else if("Plain".equals(type)){
                friendMessage.setMessage(d.selectJson("$.text").get());
            }
        });
        friendMessage.setSender(JSONObject.parseObject(json.selectJson("$.sender").get(), Sender.class));
        listener.onReceivedFirendMessage(friendMessage);
    }
}

/**
 * {
 *     "code": 0,
 *     "msg": "",
 *     "data": [
 *         {
 *             "type": "FriendMessage",
 *             "messageChain": [
 *                 {
 *                     "type": "Source",
 *                     "id": 19325,
 *                     "time": 1647482172
 *                 },
 *                 {
 *                     "type": "Plain",
 *                     "text": "撒大大"
 *                 }
 *             ],
 *             "sender": {
 *                 "id": 602378591,
 *                 "nickname": "小能",
 *                 "remark": "小能"
 *             }
 *         },
 *         {
 *             "type": "FriendInputStatusChangedEvent",
 *             "friend": {
 *                 "id": 602378591,
 *                 "nickname": "小能",
 *                 "remark": "小能"
 *             },
 *             "inputting": true
 *         },
 *         {
 *             "type": "FriendInputStatusChangedEvent",
 *             "friend": {
 *                 "id": 602378591,
 *                 "nickname": "小能",
 *                 "remark": "小能"
 *             },
 *             "inputting": false
 *         },
 *         {
 *             "type": "FriendMessage",
 *             "messageChain": [
 *                 {
 *                     "type": "Source",
 *                     "id": 19326,
 *                     "time": 1647482173
 *                 },
 *                 {
 *                     "type": "Plain",
 *                     "text": "ぃ綉滊尐瀦℃"
 *                 }
 *             ],
 *             "sender": {
 *                 "id": 602378591,
 *                 "nickname": "小能",
 *                 "remark": "小能"
 *             }
 *         }
 *     ]
 * }
 */
