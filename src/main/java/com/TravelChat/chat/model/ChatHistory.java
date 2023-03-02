package com.TravelChat.chat.model;

import lombok.Data;

@Data
public class ChatHistory {
    private int chNo;
    private int crNo;
    private int userNo;
    private String message;
    private int readCount;
    private int limit; // db에서 가져올때 사용
    private String nick;
    private int joinPoint;

}
