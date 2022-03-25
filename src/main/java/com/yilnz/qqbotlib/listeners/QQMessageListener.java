package com.yilnz.qqbotlib.listeners;

import com.yilnz.qqbotlib.entity.FriendMessage;

public interface QQMessageListener extends Listener  {
    default void onReceivedFirendMessage(FriendMessage friendMessage){};
}
