package com.yilnz.qqbotlib.entity;

import lombok.Data;

@Data
public class NewFriendRequest {
    private String eventId;
    private String fromId;
    private String groupId;
    private String nick;
    private String message;
}
