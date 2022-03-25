package com.yilnz.qqbotlib.listeners;

import com.yilnz.qqbotlib.entity.FriendMessage;
import com.yilnz.qqbotlib.entity.NewFriendRequest;
import com.yilnz.qqbotlib.entity.NewFriendRequestHandleResult;

public interface QQEventListener {
    default void onReceivedFirendMessage(FriendMessage friendMessage){};
    default NewFriendRequestHandleResult onReceivedNewFriendRequest(NewFriendRequest request){return null;};
}
