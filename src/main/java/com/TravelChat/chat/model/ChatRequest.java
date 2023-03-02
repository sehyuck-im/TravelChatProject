package com.TravelChat.chat.model;

import lombok.Data;

@Data
public class ChatRequest {
    private int requestNo;
    private int sender;
    private int receiver;
    private String photo; // sender photo
    private String nick; // sender nick
    private String code; // sender nick code
}
