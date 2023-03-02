package com.TravelChat.board.model;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PageHandlerTest {

    @Test
    void handlerTest(){
        SearchCondition sc = new SearchCondition();
        PageHandler ph = new PageHandler(250, sc);
        System.out.println("sc = " + sc);
        System.out.println("ph = " + ph);
        assertTrue(ph.getBeginPage()==1);
        assertTrue(ph.getEndPage()==10);
    }
    @Test
    void handlerTest2(){
        SearchCondition sc = new SearchCondition();
        sc.setPage(25);
        PageHandler ph = new PageHandler(255, sc);
        System.out.println("sc = " + sc);
        System.out.println("ph = " + ph);
        assertTrue(ph.getBeginPage()==21);
        assertTrue(ph.isShowNext());
        assertTrue(ph.isShowPrev());
        assertTrue(ph.getEndPage()==26);
    }
}