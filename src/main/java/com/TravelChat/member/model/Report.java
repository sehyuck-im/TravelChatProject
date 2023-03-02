package com.TravelChat.member.model;

import lombok.Data;

@Data
public class Report {
    private int reportNo;
    private int type;
    private int reason;
    private int evidence;
    private int reporter;
    private int target;
    private String chk; // 관리자가 확인했는지 여부

    // type : 1. 피드, 2. profile, 3. chat
    // reason : 1. 사진, 2. 내용, 3. 기타
    // evidence : feedNo, mNo, crNo
    private String reporterNick;
    private String targetNick;
}
