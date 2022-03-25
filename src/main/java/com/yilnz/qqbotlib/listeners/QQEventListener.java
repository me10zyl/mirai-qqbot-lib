package com.yilnz.qqbotlib.listeners;

import com.yilnz.qqbotlib.entity.FriendMessage;
import com.yilnz.qqbotlib.entity.NewFriendRequest;
import com.yilnz.qqbotlib.entity.NewFriendRequestHandleResult;
import com.yilnz.qqbotlib.entity.QQMessage;

import java.util.List;

public interface QQEventListener {
    default void onReceivedFriendMessage(FriendMessage friendMessage){};
    default NewFriendRequestHandleResult onReceivedNewFriendRequest(NewFriendRequest request){return null;};
    default List<QQMessage> postReceivedNewFriendRequest(NewFriendRequest request){return null;};
}
