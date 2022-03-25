package com.yilnz.qqbotlib.services;

import com.yilnz.qqbotlib.entity.NewFriendRequest;
import com.yilnz.qqbotlib.entity.NewFriendRequestHandleResult;
import com.yilnz.qqbotlib.httpjson.NewFriendRequestEventJson;
import com.yilnz.qqbotlib.httpjson.NewFriendRequestOperateEnum;
import com.yilnz.qqbotlib.listeners.Listener;
import com.yilnz.qqbotlib.listeners.NewFriendRequestListener;
import com.yilnz.qqbotlib.util.ApiUtil;
import com.yilnz.surfing.core.basic.Json;

public class NewFriendRequestHandler implements MsgJsonHandler {
    @Override
    public boolean support(String type) {
        return "NewFriendRequestEvent".equals(type);
    }

    @Override
    public void handle(String singleText, Listener listener, ApiUtil apiUtil) {
        if(!(listener instanceof NewFriendRequestListener)){
            return;
        }
        NewFriendRequest friendRequest = new NewFriendRequest();
        Json d = new Json(singleText);
        friendRequest.setFromId(d.selectJson("$.fromId").get());
        friendRequest.setMessage(d.selectJson("$.message").get());
        friendRequest.setEventId(d.selectJson("$.eventId").get());
        friendRequest.setNick(d.selectJson("$.nick").get());
        friendRequest.setGroupId(d.selectJson("$.groupId").get());
        NewFriendRequestHandleResult result = ((NewFriendRequestListener) listener).onReceivedNewFriendRequest(friendRequest);
        NewFriendRequestEventJson json = new NewFriendRequestEventJson();
        json.setEventId(friendRequest.getEventId());
        json.setFromId(friendRequest.getFromId());
        json.setGroupId(friendRequest.getGroupId());
        json.setOperate(result.isAccept() ? NewFriendRequestOperateEnum.AGREE : NewFriendRequestOperateEnum.REJECT);
        json.setMessage(result.getMessage());
        apiUtil.handleNewFriendRequestEvent(json);
    }
}
