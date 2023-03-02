package com.TravelChat.chat.model;

import lombok.Data;

@Data
public class ChatUser {
    private int userNo;
    private int crNo;
    private int mNo;
    private int chNo;
    private int joinPoint;
    private String status;

    private String nick;
    private String code;
    private String photo;

}
