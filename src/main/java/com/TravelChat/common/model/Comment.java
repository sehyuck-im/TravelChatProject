package com.TravelChat.common.model;

import lombok.Data;

import java.util.Date;

@Data
public class Comment {
    private int cno;
    private int feedNo;
    private int pcno;
    private int level;
    private int writer;
    private String content;
    private Date createTime;
    private String del;

    private String nick;
    private String code;
    private String photo;
}
