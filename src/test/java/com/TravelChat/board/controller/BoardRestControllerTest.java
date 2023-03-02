package com.TravelChat.board.controller;

import com.TravelChat.board.model.Board;
import com.TravelChat.board.service.BoardService;
import com.TravelChat.chat.model.ChatRoom;
import com.TravelChat.chat.repository.ChatRoomRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
class BoardRestControllerTest {

    @Autowired
    BoardService boardService;
    @Autowired
    ChatRoomRepository chatRoomRepository;

    @Test
    void insertMockBoard() throws Exception {
        Board board = new Board();
        for(int i=1; i<=155; i++){
            board.setWriter(2);
            board.setContent("title"+i);
            board.setTitle("content"+i);
            boardService.insertBoard(board);

        }
    }

    @Test
    void insertAndCreateTestWithTx() {
        Board board = new Board();
        board.setTitle("Test Tx");
        board.setContent("It's Test Tx Content");
        board.setWriter(2);

        try {
            boardService.insertBoardAndCreateChatRoom(board);
        } catch (Exception e) {
            System.out.println("e.getMessage() = " + e.getMessage());
        }

    }
}