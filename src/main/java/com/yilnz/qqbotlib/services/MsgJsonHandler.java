package com.yilnz.qqbotlib.services;

import com.yilnz.qqbotlib.QQMessageListener;

public interface MsgJsonHandler {

    boolean support(String type);

    void handle(String singleText, QQMessageListener listener);
}
