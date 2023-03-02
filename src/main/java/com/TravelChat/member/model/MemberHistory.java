package com.TravelChat.member.model;

import lombok.Data;

import java.util.Date;

@Data
public class MemberHistory {
    private int hNo;
    private int mNo;
    private String email;
    private String nick;
    private String gender;
    private String status;
    private int reportCnt;
    private Date regDate;
    private Date leaveDate;

}
