package com.TravelChat.member.model;

import lombok.Data;

import java.util.Date;

@Data
public class Member {
    private int mNo;
    private String email;
    private String nick; // 뒤에 #무작위 난수 4개주기
    private String password;
    private String gender;
    private String status; // 회원상태
    private Date regDate;

    private int reportCount;
    private String photo;
    private String code;
    private String stringDate;
}