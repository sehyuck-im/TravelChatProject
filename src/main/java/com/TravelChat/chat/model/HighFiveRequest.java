package com.TravelChat.chat.model;

import lombok.Data;

@Data
public class HighFiveRequest {

    private int requestNo;
    private int crNo;
    private int sender;
    private int receiver;

    private String nick;
    private String code;
    private String photo;
    private String title;
}
