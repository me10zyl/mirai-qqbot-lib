package com.yilnz.qqbotlib.entity;

import lombok.Data;

import java.util.Date;

@Data
public class FriendMessage {
    private Date time;
    private String message;
    private Sender sender;
}
