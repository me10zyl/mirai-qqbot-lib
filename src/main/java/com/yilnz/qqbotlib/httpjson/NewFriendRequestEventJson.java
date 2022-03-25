package com.yilnz.qqbotlib.httpjson;

import lombok.Data;

@Data
public class NewFriendRequestEventJson {
    private String eventId;
    private String fromId;
    private String groupId;
    private Integer operate;
    private String message;
}
