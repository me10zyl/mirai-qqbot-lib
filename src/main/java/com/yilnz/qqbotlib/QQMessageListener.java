package com.yilnz.qqbotlib;

import com.yilnz.qqbotlib.entity.FriendMessage;

public interface QQMessageListener {
    default void onReceivedFirendMessage(FriendMessage friendMessage){};
}
