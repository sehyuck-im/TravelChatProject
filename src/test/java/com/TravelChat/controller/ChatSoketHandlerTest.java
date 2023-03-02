package com.TravelChat.controller;

import com.TravelChat.chat.service.ChatHistoryService;
import com.TravelChat.chat.service.ChatRoomService;
import com.TravelChat.chat.service.ChatUserService;
import com.TravelChat.common.service.TestService;
import com.TravelChat.member.service.MemberService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class ChatSocketHandlerTest {

    @Autowired
    private MemberService ms;
    @Autowired
    private ChatUserService cus;
    @Autowired
    private TestService ts;
    @Autowired
    private ChatHistoryService chs;
    @Autowired
    private ChatRoomService crs;

    /**
     * chatUserSetter의 기본값
     * chNo(마지막으로 읽은 메세지)=20
     * crNo(채팅방 번호)=124
     * mno(멤버번호)=10
     */
    @Test
    void 채팅방입장시메세지불러오기() {
        /**
         * 1.채팅방에 입장을 한다
         * 2.채팅방에 입장시 과거메세지는 위에
         * 3.채팅방 입장시 안읽은 메세지는 스크롤 반응을 안하게 불러와야되기 때문에 실제 서비스 에서는 따로 리스트로 받아 2번에 걸쳐 보내준다
         */
        //given
//        ChatUser chatUserTemp = ts.chatUserSetter();//신규 쳇유저 셋
//        int result = cus.insert(chatUserTemp);//쳇유저 입력
//        if (result == 1) {
//            System.out.println("테스트 유저 입력 성공");
//            result = 0;
//        }
//        ChatUser chatUser = ts.selectTempUser(chatUserTemp);//입력했던 쳇유저 불러오기
//        System.out.println("여기까지 읽음" + chatUserTemp.getChNo());
//        ts.insertTempChatMessage(chatUser);//채팅 더미데이터 입력 (100개)
//        List<ChatHistory> pastMsgList = chs.selectPastMsg(chatUserTemp.getChNo(), chatUser.getCrNo());
//        List<ChatHistory> lateMsgList = chs.selectLateMsg(chatUserTemp.getChNo(), chatUser.getCrNo());
//        //when
//        int pastCount = 0;
//        int lateCount = 0;
//        for (ChatHistory past : pastMsgList) {
//            System.out.println(past.getMessage());
//            pastCount++;
//        }
//        System.out.println("<여기부터>");
//        for (ChatHistory late : lateMsgList) {
//            System.out.println(late.getMessage());
//            lateCount++;
//        }
//        //then
//        if (pastCount == 19) {
//            System.out.println("읽은 메세지 불러오기 성공");
//        }
//        if (lateCount == 81) {
//            System.out.println("읽지 않은메세지 불러오기 성공");
//        }
    }

    @Test
    void 채팅방생성후메세지전송() {
        /** 채팅방을 생성한 후에 입장한 회원들에게만 메세지를 전송하는 method
         * 1. 채팅방에 5명이 입장해 있다.
         * 2. 세명은 채팅방에 있고 두명은 채팅방을 보고 있지 않다.
         * 3. 입장해 있는 2명에게 입장한 사람중 한명이 메세지를 보낸다.
         * 4. 보낸 사람의 chNo이 업데이트 되어야한다.
         * 5. 입장해 있는 2명의 메세지의 상태가 update 되어야한다.
         * 6. 나머지 2명은 읽지 않은 메세지로 되어있어야한다.
         */
        //given

//        for (int i = 1; i <= 5; i++) { // 5명 입장
//            ts.insertFiveChatUser(i);
//        }
//        List<ChatUser> cuList = ts.selectFive(); //채팅방 유저 List
//        List<ChatUser> threeList = ts.selectThree(); // 입장해 있는 3명이 있는 List
//
//        //when
//        for (int i = 1; i <= 5; i++) { // 메세지를 5개 보낸다.
//            ts.insertFiveChatHistory(i); //채팅 히스토리 입력
//        }
//        for (ChatUser chatUser : cuList) {  // 5번 반복 실행
//            if (threeList.contains(chatUser)) { // 채팅방에 입장해 있는 사람에게 메세지 전송(update)
//                ChatHistory chatHistory = ts.selectLastMsg();
//                chatUser.setChNo(chatHistory.getChNo());
//                ts.updateThreeChatUser(chatUser); // 마지막 읽은 메세지 업데이트
//                ts.updateReadCount(chatHistory); // 채팅방 readCount 업데이트
//            }
//        }
//        //then 업데이트가 잘 되었는지 확인
//        cuList = ts.selectFive(); //채팅방 유저 List
//        threeList = ts.selectThree(); // 입장해 있는 3명이 있는 List
//        int sCount = 0; // 성공 개수
//        for (ChatUser chatUser : cuList) { // 채팅방에 입장해 있는 사람에게 메세지 전송(update)
//            if (threeList.contains(chatUser)) {
//                ChatHistory chatHistory = ts.selectLastMsg();
//                if (chatUser.getChNo() == chatHistory.getChNo()) {
//                    sCount++;
//                }
//                System.out.println("성공 개수 : " + sCount);
//            } else {
//                if (chatUser.getChNo() == 1) {
//                    sCount++;
//                }
//                System.out.println("성공 개수 : " + sCount);
//            }
//        }
//        if (sCount == 5) {
//            System.out.println("테스트 성공");
//        } else {
//            System.out.println("테스트 실패");
//        }
    }

    @Test
    void 메세지전송() throws Exception {
        /**
         *  1. 채팅방을 만들고 6명 유저를 넣는다.
         *  2. String List를 만들고 채팅방, 채팅리스트, 페이지에 있는 사람을 각각 리스트에 넣는다
         *  3. 리스트별로 맞는 메세지를 보낸다 (옵션에 맞춰서)
         *  4. 잘 갔는지 확인
         *
         *  채팅방에 있는 사람 : 메세지 내용 + 보낸 사람
         *  채팅리스트에 있는 사람 : 읽지 않은 메세지 카운트(방별) + 마지막 메세지
         *  페이지에 있는 사람 읽지 않은 메세지 카운트 (총 카운트)
         */

        //given
        /**
         *  1. 채팅방
         *  2. 유저 6명
         */
//        //채팅방 생성
//        crs.createChatRoom();
//        ChatRoom chatRoom = crs.selectChatRoom();
//
//        // 유저 6명 채팅방에 입장
//        for (int i = 0; i <= 6; i++) {
//            ts.insertSixUser(chatRoom.getCrNo(), i);
//        }
//
//        //when
//        // List를 만들고 리스트에 추가하기
//        List<String> webSocket = new ArrayList<>();
//        List<ChatUser> cuList = ts.selectUser(chatRoom.getCrNo()); // 채팅방에 입장해있는 사람들 리스트
//        int count = 0;
//        for (ChatUser chatUser : cuList) { // webSocket에 등록
//            count++;
//            if (count == 1 || count == 2) { //채팅방에 들어와있는 사람 2명
//                String mNo = Integer.toString(chatUser.getMNo());
//                webSocket.add(mNo);
//            }
//            if (count == 3 || count == 4) { //채팅리스트에 있는 사람 2명
//                String userNo = Integer.toString(chatUser.getUserNo());
//                webSocket.add(userNo);
//            }
//            if (count == 5) { // 페이지에 있는 사람 1명
//                String userNo = Integer.toString(chatUser.getUserNo());
//                userNo += "Header";
//                webSocket.add(userNo);
//            }
//            //서비스를 이용하지 않는 사람 1명은 아무것도 안함
//        }
//
//        ts.simpleMsg(chatRoom.getCrNo());
//        ChatHistory chatHistory = ts.selectSimpleMsg(chatRoom.getCrNo());
//        List<String> users = new ArrayList<>();
//
//        for(ChatUser chatUser : cuList){ // 상황별 list 나누기
//            String mNo = Integer.toString(chatUser.getMNo());
//            String userNo = Integer.toString(chatUser.getUserNo());
//            String header = Integer.toString(chatUser.getUserNo());
//            header += "Header";
//            String msg = "";
//            if(webSocket.contains(mNo)){ // 채팅방에 들어와 있는 사람
//                msg += mNo;
//                msg += " : ";
//                msg += chatHistory.getUserNo();
//                msg += " : ";
//                msg += chatHistory.getMessage();
//                msg += " : ";
//                msg += " 채팅방 ";
//                users.add(msg);
//            }
//            if(webSocket.contains(userNo)){ // 채팅 리스트에 있는 사람
//                msg += userNo;
//                msg += " : ";
//                msg += chatHistory.getMessage();
//                msg += " : ";
//                msg += " 1 ";
//                msg += " : ";
//                msg += chatHistory.getCrNo();
//                msg += " : ";
//                msg += " 리스트 ";
//                users.add(msg);
//            }
//            if(webSocket.contains(header)){ // 페이지에 있는 사람
//                msg += header;
//                msg += " : ";
//                msg += " 1 ";
//                msg += " : ";
//                msg += " header ";
//                users.add(msg);
//            }
//        }
//        //then
//        for(String message : users){
//            System.out.println(message);
//        }
        
    }
}