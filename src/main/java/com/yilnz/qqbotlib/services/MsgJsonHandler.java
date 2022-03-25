package com.yilnz.qqbotlib.services;

import com.yilnz.qqbotlib.listeners.Listener;
import com.yilnz.qqbotlib.util.ApiUtil;

public interface MsgJsonHandler {

    boolean support(String type);

    void handle(String singleText, Listener listener, ApiUtil apiUtil);
}
