package com.yilnz.qqbotlib.httpjson;

import lombok.Data;

@Data
public class NewFriendRequestEventJson {
    private Long eventId;
    private Long fromId;
    private Long groupId;
    private Integer operate;
    private String message;
}
