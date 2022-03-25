package com.yilnz.qqbotlib.services;

import com.yilnz.qqbotlib.entity.NewFriendRequest;
import com.yilnz.qqbotlib.entity.NewFriendRequestHandleResult;
import com.yilnz.qqbotlib.entity.QQMessage;
import com.yilnz.qqbotlib.httpjson.NewFriendRequestEventJson;
import com.yilnz.qqbotlib.httpjson.NewFriendRequestOperateEnum;
import com.yilnz.qqbotlib.listeners.QQEventListener;
import com.yilnz.qqbotlib.util.ApiUtil;
import com.yilnz.surfing.core.basic.Json;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

//[{"nick":"zSkyRaker","eventId":1648178457000000,"groupId":829987644,"type":"NewFriendRequestEvent","message":"","fromId":602077597}]
public class NewFriendRequestHandler implements MsgJsonHandler {
    @Override
    public boolean support(String type) {
        return "NewFriendRequestEvent".equals(type);
    }

    @Override
    public void handle(String singleText, QQEventListener listener, ApiUtil apiUtil) {

        NewFriendRequest friendRequest = new NewFriendRequest();
        Json d = new Json(singleText);
        friendRequest.setFromId(d.selectJson("$.fromId").getLong());
        friendRequest.setMessage(d.selectJson("$.message").get());
        friendRequest.setEventId(d.selectJson("$.eventId").getLong());
        friendRequest.setNick(d.selectJson("$.nick").get());
        friendRequest.setGroupId(d.selectJson("$.groupId").getLong());
        NewFriendRequestHandleResult result = listener.onReceivedNewFriendRequest(friendRequest);
        if (result != null) {
            NewFriendRequestEventJson json = new NewFriendRequestEventJson();
            json.setEventId(friendRequest.getEventId());
            json.setFromId(friendRequest.getFromId());
            json.setGroupId(friendRequest.getGroupId());
            json.setOperate(result.isAccept() ? NewFriendRequestOperateEnum.AGREE : NewFriendRequestOperateEnum.REJECT);
            json.setMessage(result.getMessage());
            apiUtil.handleNewFriendRequestEvent(json);
            List<QQMessage> qqMessageList = listener.postReceivedNewFriendRequest(friendRequest);
            if(qqMessageList != null) {
                new ScheduledThreadPoolExecutor(1).schedule(new Runnable() {
                    @Override
                    public void run() {
                        apiUtil.sendFriendMessage(String.valueOf(friendRequest.getFromId()), qqMessageList);
                    }
                }, 5, TimeUnit.SECONDS);
            }
        }
    }
}
