package com.TravelChat.member.model;

import lombok.Data;

@Data
public class ShakeRequest {
    private int shakeNo;
    private int sender;
    private int receiver;
    private String photo; // sender photo
    private String nick; // sender nick
    private String code; // sender nick code
}
