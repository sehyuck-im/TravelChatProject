package com.TravelChat.board.model;

import lombok.Data;

@Data
public class Board {
    private int bNo;
    private int crNo;
    private String title;
    private int writer;
    private String content;
    private String bDel;

    private String nick;
    private String code;
    private String photo;
}
