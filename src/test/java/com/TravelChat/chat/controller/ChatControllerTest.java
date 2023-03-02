package com.TravelChat.chat.controller;

import com.TravelChat.chat.model.ChatRoom;
import com.TravelChat.chat.model.ChatUser;
import com.TravelChat.chat.service.ChatHistoryService;
import com.TravelChat.chat.service.ChatRoomService;
import com.TravelChat.chat.service.ChatService;
import com.TravelChat.chat.service.ChatUserService;
import com.TravelChat.member.service.MemberService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.servlet.http.HttpSession;

import java.util.List;

@SpringBootTest
class ChatControllerTest {
    @Autowired
    private MemberService ms;
    @Autowired
    private HttpSession session;
    @Autowired
    private ChatService chatService;
    @Autowired
    private ChatUserService chatUserService;
    @Autowired
    private ChatHistoryService chatHistoryService;
    @Autowired
    private ChatRoomService chatRoomService;

    @Test
    void accessChatRoomTest(){
        int mNo = 20;
        int crNo = 2;
        int cnt = chatUserService.countUserNoByMNoAndCrNo(mNo, crNo);

        System.out.println("cnt = " + cnt);

    }

    @Test
    void chatLeaveTest(){
        int mNo = 2;
        int crNo = 8;
        int userNo = 16;
        //1. mNo으로 가져온 userList 중에 userNo이 겹치고 crNo이 맞다면 채팅방 떠나기 활성화
        List<ChatUser> chatUserList = chatUserService.selectChatUserListByMNo(mNo);
        for(ChatUser chatUser : chatUserList){
            if(chatUser.getUserNo() == userNo && chatUser.getCrNo() == crNo){
                System.out.println("chatUser = " + chatUser);
                try {
                    String nick = chatUserService.getUserNick(chatUser.getUserNo());
                    chatUser.setNick(nick);
                    chatUser.setJoinPoint(1); // readCnt를 joinPoint에 set 해주기
//                    int statusResult = chatUserService.changeStatusOut(chatUser);
//                    int msgResult = chatHistoryService.sendOutMsg(chatUser);
//                    if(statusResult != 1){
//                        throw new Exception("FAILED_STATUS");
//                    }
//                    if(msgResult != 1){
//                        throw new Exception("FAILED_MSG");
//                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @Test
    void findChatRoomByMNo(){
        int mNo1 = 4;
        int mNo2 = 2;

        List<ChatUser> chatUserList1 = chatUserService.selectChatUserListByMNo(mNo1);
        List<ChatUser> chatUserList2 = chatUserService.selectChatUserListByMNo(mNo2);
        int crNo = 0;
        for(ChatUser myChatUser : chatUserList1) {
            ChatRoom chatRoom = chatRoomService.selectChatRoomByCrNo(myChatUser.getCrNo());
            for (ChatUser receiverUser : chatUserList2) {
                if (receiverUser.getCrNo() == myChatUser.getCrNo() && chatRoom.getGroupChat().equals("n")) {
                    //둘다 참여 중이고 개인채팅방인 경우
                    crNo = receiverUser.getCrNo();
                    System.out.println("crNo = " + crNo);
                    int userNo1 = receiverUser.getUserNo();
                    int userNo2 = myChatUser.getUserNo();
                    System.out.println("userNo1 = " + userNo1);
                    System.out.println("userNo2 = " + userNo2);
                }
            }

        }
    }

    @Test
    void findChatRoomByUserNo(){
        int userNo1 = 3;
        int userNo2 = 4;



    }

}