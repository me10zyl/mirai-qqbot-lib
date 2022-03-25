package com.yilnz.qqbotlib.listeners;

import com.yilnz.qqbotlib.entity.NewFriendRequest;
import com.yilnz.qqbotlib.entity.NewFriendRequestHandleResult;

public interface NewFriendRequestListener extends Listener {
    NewFriendRequestHandleResult onReceivedNewFriendRequest(NewFriendRequest request);
}
