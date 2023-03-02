package com.TravelChat.chat.controller;

import com.TravelChat.chat.model.ChatHistory;
import com.TravelChat.chat.model.ChatRoom;
import com.TravelChat.chat.model.ChatRoomSet;
import com.TravelChat.chat.model.ChatUser;
import com.TravelChat.chat.service.ChatHistoryService;
import com.TravelChat.chat.service.ChatRoomService;
import com.TravelChat.chat.service.ChatService;
import com.TravelChat.chat.service.ChatUserService;
import com.TravelChat.member.service.MemberService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
class ChatWebSocketHandlerTest {
    @Autowired
    private ChatHistoryService chatHistoryService;
    @Autowired
    private ChatUserService chatUserService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private ChatRoomService chatRoomService;
    @Autowired
    private ChatService chatService;

    @Test
    void chatSendTest() {
        List<String> users = new ArrayList();
        Map<String, Object> userMap = new HashMap<String, Object>();
        // users에 session 저장

        // 1. 4명이 websocket session에 접속
        for (int i = 1; i <= 4; i++) {
            users.add(i + "번째 session");
        }

        // 2. 3명이 chatRoom에 들어와 있고 1명은 chatList에 있음
        // 1,2,3은 chatRoom, 4는 chatList, 4명 모두 같은 채팅방이 있음
        for (int i = 1; i <= 4; i++) {
            if (i == 4) {
                userMap.put(i + "List", users.get(i - 1));
            } else {
                userMap.put(i + "Room", users.get(i - 1));
            }
        }
        System.out.println("users = " + users);
        System.out.println("userMap = " + userMap);


        String userNo = "2";
        String content = "Test sent by 2";
        for (int i = 1; i <= 4; i++) {
            int IUserNo = Integer.parseInt(userNo);
            String chatRoom = i + "Room";
            String chatRoomList = i + "List";
            String chatHeader = i + "Header";
            if (users.contains(userMap.get(chatRoom))) {

                if (IUserNo == i) {
                    System.out.println("본인이 보낸 메세지");
                } else {
                    System.out.println("content = " + content);
                }
            }
        }

    }

    @Test
    @Transactional
    void chatInsertReturnTest() throws Exception {
        ChatHistory chatHistory = new ChatHistory();
        chatHistory.setCrNo(2);
        chatHistory.setReadCount(1);
        chatHistory.setUserNo(4);
        chatHistory.setMessage("Test Test");

        chatHistory = chatHistoryService.insertMessage(chatHistory);


        System.out.println("chatHistory = " + chatHistory);

    }

    @Test
    void divideReceiverAndSender() {
        List<ChatUser> chatUsers = chatUserService.selectByCrNo(2);
        for (int i = 0; i < chatUsers.size(); i++) { // 닉네임 분리작업
            //nick # 분리
            String[] nickAndCode = memberService.separateNick(chatUsers.get(i).getNick());
            chatUsers.get(i).setNick(nickAndCode[0]);
            chatUsers.get(i).setCode(nickAndCode[1]);
        }

    }

    @Test
    void calUserAtChatRoom() {
        List<String> users = new ArrayList();
        Map<String, Object> userMap = new HashMap<String, Object>();
        // users에 session 저장
        // 1. 4명이 websocket session에 접속
        for (int i = 1; i <= 4; i++) {
            users.add(i + "번째 session");
        }

        // 2. 3명이 chatRoom에 들어와 있고 1명은 chatList에 있음
        // 1,2,3은 chatRoom, 4는 chatList, 4명 모두 같은 채팅방이 있음
        for (int i = 1; i <= 4; i++) {
            if (i == 4) {
                userMap.put(i + "List", users.get(i - 1));
            } else {
                userMap.put(i + "Room", users.get(i - 1));
            }
        }
        System.out.println("users = " + users);
        System.out.println("userMap = " + userMap);

        int cnt = 0;
        String userNo = "2";
        String content = "Test sent by 2";

        for (int i = 1; i <= userMap.size(); i++) {

            String chatRoom = i + "Room";
            if (users.contains(userMap.get(chatRoom))) {
                cnt++;
            }
        }
        System.out.println("cnt = " + cnt);

    }

    @Test
    void lastMessageSelectTest() {
        int crNo = 2;
        ChatHistory lastChat = chatHistoryService.selectLastChatAtChatRoom(crNo);
        System.out.println("lastChat = " + lastChat);

    }

    @Test
    void chatListTest() {
        List<String> users = new ArrayList();
        Map<String, Object> userMap = new HashMap<String, Object>();
        String session = "testSession";
        int mNo = 2;
        users.add(mNo + "의 세션 session");

        // 채팅방 리스트 입장시
        List<ChatUser> myList = chatUserService.selectChatUserListByMNo(mNo);
        for(ChatUser chatUser : myList){
            String userNo = chatUser.getUserNo()+"List";
            userMap.put(userNo, session);
        }
        String chatRoomShape = "";
        System.out.println("myList = " + myList);
        for (ChatUser chatUser : myList) { // 해당 member가 참가한 userList
//            String user = chatUser.getUserNo() + "List";
//            WebSocketSession ws = (WebSocketSession) userMap.get(user);
            int crNo = chatUser.getCrNo();
            int myUserNo = chatUser.getUserNo();
            int unreadCnt = chatHistoryService.getUnreadCnt(chatUser);
            ChatRoom chatRoom = chatRoomService.selectChatRoomByCrNo(crNo);

            // 해당 member를 포함한 같은 채팅방에 있는 유저 리스트
            List<ChatUser> chatUserList = setChatUsersWithProfileAndNickCode(crNo);

            // 전달용 model
            ChatRoomSet chatRoomSet = new ChatRoomSet();
            chatRoomSet.setMyUserNo(myUserNo);
            chatRoomSet.setUnreadCnt(unreadCnt);

            // 마지막 메세지가 시스템 메세지면 lastChat에 아무것도 안들어옴
            // 마지막 메세지의 userNo 이 0이면 따로 설정해야함
            int userNoChecker = chatHistoryService.selectLastMsgSendUserNo(crNo);
            if (userNoChecker == 0) { // 시스템 메세지
                chatRoomSet.setLastNick("system");
                String systemMsg = chatHistoryService.selectSystemMsgByCrNo(crNo);
                chatRoomSet.setLastMsg(systemMsg);
            } else {
                // 마지막 메세지, nick set
                ChatHistory lastChat = chatHistoryService.selectLastChatAtChatRoom(crNo);
                chatRoomSet.setLastNick(lastChat.getNick());
                chatRoomSet.setLastMsg(lastChat.getMessage());
            }

            if (chatRoom.getGroupChat().equals("y")) { // 단체 채팅방
                // 채팅방 이름 불러오기
                chatRoomSet.setRoomTitle(chatRoom.getCrTitle());

            } else { // 1:1 채팅방
                for (ChatUser otherUser : chatUserList) { // 상대방 닉네임 가져오기
                    if (otherUser.getUserNo() != chatUser.getUserNo()) {
                        chatRoomSet.setRoomTitle(otherUser.getNick() + otherUser.getCode());
                    }
                }

            }
            System.out.println("chatUserList = " + chatUserList);
            System.out.println("chatRoomSet = " + chatRoomSet);

            chatRoomShape = chatService.setChatRoom(chatUserList, chatRoomSet);
            chatRoomShape += "<div data=\"chatRoomList\"></div>";

            System.out.println("chatRoomShape = " + chatRoomShape);

        }

    }

    @Test
    @Transactional
    void leaveChatRoomWithTx(){
        List<String> users = new ArrayList();
        Map<String, Object> userMap = new HashMap<String, Object>();
        // users에 session 저장

        // 1. 4명이 websocket session에 접속
        for (int i = 18; i <= 22; i++) {
            users.add(i + " session");
        }

        // 2. 3명이 chatRoom에 들어와 있고 1명은 chatList에 있음
        // 18 19 20 은 chatRoom, 21는 chatList, 4명 모두 같은 채팅방이 있음
        for (int i = 18; i <= 21; i++) {
            if (i == 21) {
                userMap.put(i + "List", users.get(i - 18));
            } else {
                userMap.put(i + "Room", users.get(i - 18));
            }
        }
        System.out.println("users = " + users);
        System.out.println("userMap = " + userMap);


        int crNo = 9;
        int userNo = 18;
        int mNo = 2;

        List<ChatUser> chatUserList = setChatUsersWithProfileAndNickCode(crNo);
        int readCnt = chatUserService.getCountAtChatRoom(crNo);

        for (ChatUser chatUser : chatUserList) {
            String chatRoom = chatUser.getUserNo() + "Room";
            if (users.contains(userMap.get(chatRoom))) {
                readCnt--;
            }
        }
        System.out.println("readCnt = " + readCnt);
        ChatHistory chatHistory = new ChatHistory();
        for(ChatUser chatUser : chatUserList){
            if(chatUser.getUserNo() == userNo && chatUser.getCrNo() == crNo){
                try {
                    String nick = chatUserService.getUserNick(chatUser.getUserNo());
                    chatUser.setNick(nick);
                    chatUser.setJoinPoint(readCnt); // joinPoint에 readCnt 담아 보내기
                    chatHistory = chatUserService.leaveChatRoomTransaction(chatUser);
                    chatRoomService.updateTimeAtChatRoom(chatUser.getCrNo());

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
        ChatUser chatUser = chatUserService.selectByUserNo(userNo);
        ChatRoom chatRoom = chatRoomService.selectChatRoomByCrNo(crNo);
        ChatHistory updatedHistory = chatHistoryService.selectLastChatAtChatRoom(crNo);

        System.out.println("updatedHistory = " + updatedHistory);
        System.out.println("chatRoom = " + chatRoom);
        System.out.println("chatUser = " + chatUser);
        System.out.println("new chatHistory = " + chatHistory);

        userMap.remove(userNo+"Room");
        System.out.println("userMap = " + userMap);
    }

    @Test
    void ChatUserSelectByCrNo(){
        int crNo = 2;

        List<ChatUser> chatUserListAtChatRoom = chatUserService.selectByCrNo(crNo);
        List<ChatUser> chatUserList = chatUserService.selectChatUserListByMNo(2);

        System.out.println("chatUserList = " + chatUserList);
        System.out.println("chatUserListAtChatRoom = " + chatUserListAtChatRoom);
        for(ChatUser chatUser : chatUserList){
            for(ChatUser atChatRoom : chatUserListAtChatRoom){
                if(atChatRoom.getCrNo() == chatUser.getCrNo()){
//                    boolean chk = chatUserListAtChatRoom.remove(atChatRoom);
//                    System.out.println("chk = " + chk);
                    System.out.println("chatUser.getUserNo() = " + chatUser.getUserNo());
                }
            }
        }
        System.out.println("after for");
        System.out.println("chatUserList = " + chatUserList);
        System.out.println("chatUserListAtChatRoom = " + chatUserListAtChatRoom);

    }

    @Test
    void findUserLocation(){
        List<String> users = new ArrayList();
        Map<String, Object> userMap = new HashMap<String, Object>();

        List<ChatUser> chatUserList1 = chatUserService.selectChatUserListByMNo(1);
        List<ChatUser> chatUserList2 = chatUserService.selectChatUserListByMNo(2);
        List<ChatUser> chatUserList11 = chatUserService.selectChatUserListByMNo(11);
        users.add("1session");
        users.add("2session");
        users.add("11session");
        String session1 = "1session";
        String session2 = "2session";
        String session11 = "11session";

        for(ChatUser chatUser : chatUserList1){

            userMap.put(chatUser.getUserNo()+"List",session1);
        }
        for(ChatUser chatUser : chatUserList2){
            userMap.put(chatUser.getUserNo()+"List",session2);
        }
        for(ChatUser chatUser : chatUserList11){
            userMap.put(chatUser.getUserNo()+"List",session11);
        }

        System.out.println("users = " + users);
        System.out.println("userMap = " + userMap);

//        for(ChatUser chatUser : chatUserList11){
//            if(userMap.containsKey(chatUser.getUserNo()+"List")){
//                System.out.println("list에 있음");
//            }
//        }
        boolean isFound = false;
        int i = 1;
        for(ChatUser chatUser : chatUserList11){
            System.out.println("i = " + i);
            if(userMap.containsKey(chatUser.getUserNo()+"List")){
                System.out.println("list에 있음");
                isFound = true;
                i++;
                break;
            }
//            if(isFound){
//                break;
//            }
        }

    }

    @Test
    void chatHeaderTest(){
        List<String> users = new ArrayList();
        Map<String, Object> userMap = new HashMap<String, Object>();

        List<ChatUser> chatUserList1 = chatUserService.selectChatUserListByMNo(1);
        List<ChatUser> chatUserList2 = chatUserService.selectChatUserListByMNo(2);
        List<ChatUser> chatUserList11 = chatUserService.selectChatUserListByMNo(11);
        users.add("1session");
        users.add("2session");
        users.add("11session");
        String session1 = "1session";
        String session2 = "2session";
        String session11 = "11session";

        for(ChatUser chatUser : chatUserList1){

            userMap.put(chatUser.getUserNo()+"List",session1);
            userMap.put(chatUser.getMNo()+"Header", session1);
        }
        for(ChatUser chatUser : chatUserList2){
            userMap.put(chatUser.getUserNo()+"List",session2);

        }
        for(ChatUser chatUser : chatUserList11){
            userMap.put(chatUser.getUserNo()+"List",session11);

        }

        System.out.println("users = " + users);
        System.out.println("userMap = " + userMap);

        for(ChatUser chatUser : chatUserList1){

            if(users.contains(userMap.get(chatUser.getUserNo()+"List"))){
                System.out.println("list session에 있음");
            }
            if(users.contains(userMap.get(chatUser.getMNo()+"Header"))){
                System.out.println("header 도 있음");
            }
        }

    }


    private List<ChatUser> setChatUsersWithProfileAndNickCode(int crNo) {
        List<ChatUser> userList = chatUserService.selectByCrNo(crNo);

        for (int i = 0; i < userList.size(); i++) { // 닉네임 분리작업
            //nick # 분리
            String[] nickAndCode = memberService.separateNick(userList.get(i).getNick());
            userList.get(i).setNick(nickAndCode[0]);
            userList.get(i).setCode(nickAndCode[1]);
        }
        return userList;
    }


}