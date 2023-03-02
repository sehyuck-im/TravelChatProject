package com.TravelChat.common.model;

import lombok.Data;

@Data
public class Email {
    private final String FROM = "travelPlannerOfficial@gmail.com";
    private String to;
    private String title;
    private String content;
    private String code;
    private String newPass;
    private String reason;

}
