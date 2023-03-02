package com.TravelChat.chat.model;

import lombok.Data;

import java.util.Date;

@Data
public class ChatRoom {
    private int crNo;
    private String crTitle;
    private String groupChat;
    private int admin;
    private Date updateTime;
    private String crDel;

}
