package com.TravelChat.chat.model;

import lombok.Data;

import java.util.Date;

@Data
public class ChatRoomDeliver extends ChatRoom{
    private int count;
    private int chatCount;

}
