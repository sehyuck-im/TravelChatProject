package com.TravelChat.admin.model;

import lombok.Data;

@Data
public class ReportHistory {
    private int mNo;
    private int feed;
    private int profile;
    private int chat;
    private int suspendCount;
}
