package com.yilnz.qqbotlib.entity;

import lombok.Data;

@Data
public class NewFriendRequest {
    private Long eventId;
    private Long fromId;
    private Long groupId;
    private String nick;
    private String message;
}
