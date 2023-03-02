package com.TravelChat.chat.model;

import lombok.Data;

@Data
public class ChatRoomSet {
    // chat list 전달용 model
    private String roomTitle;
    private int myUserNo;
    private int unreadCnt;
    private String lastNick;
    private String lastMsg;
}
