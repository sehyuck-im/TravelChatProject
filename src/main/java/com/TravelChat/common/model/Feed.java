package com.TravelChat.common.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class Feed {
    private int feedNo;
    private int writer;
    private String photo;
    private String content;
    private int commentCount;
    private Date createTime;
    private String del;
    private String stringDate;

    private List<String> photoNames = new ArrayList<>();
    private List<Comment> commentList = new ArrayList<>();
}
