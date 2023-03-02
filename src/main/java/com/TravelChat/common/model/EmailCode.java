package com.TravelChat.common.model;

import lombok.Data;

@Data
public class EmailCode {
    private String email;
    private String code;

    public EmailCode(String email, String code) {
        this.email = email;
        this.code = code;
    }

    public EmailCode() {

    }
}
