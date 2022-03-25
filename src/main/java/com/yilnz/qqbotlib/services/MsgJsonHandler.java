package com.yilnz.qqbotlib.services;

import com.yilnz.qqbotlib.listeners.QQEventListener;
import com.yilnz.qqbotlib.util.ApiUtil;

public interface MsgJsonHandler {

    boolean support(String type);

    void handle(String singleText, QQEventListener listener, ApiUtil apiUtil);
}
