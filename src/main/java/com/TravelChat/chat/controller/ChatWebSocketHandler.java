package com.TravelChat.chat.controller;


import com.TravelChat.chat.model.*;
import com.TravelChat.chat.service.*;
import com.TravelChat.member.model.Member;
import com.TravelChat.member.model.ShakeRequest;
import com.TravelChat.member.service.MemberService;
import com.TravelChat.member.service.ShakerService;
import groovy.util.logging.Log4j2;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Log4j2
public class ChatWebSocketHandler extends TextWebSocketHandler {

    @Autowired
    private ChatHistoryService chatHistoryService;
    @Autowired
    private ChatUserService chatUserService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private ChatService chatService;
    @Autowired
    private ChatRoomService chatRoomService;
    @Autowired
    private ShakerService shakerService;


    private List<WebSocketSession> users;
    private Map<String, Object> userMap;


    public ChatWebSocketHandler() {
        users = new ArrayList<WebSocketSession>();
        userMap = new HashMap<String, Object>();
    }

    public void afterConnectionEstablished(WebSocketSession session) throws Exception {//연결
        if(session!=null){

            users.add(session);//웹소켓 세션에 유저 등록
        }else{
            users.remove(session);
        }

    }

    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        JSONObject object = new JSONObject(message.getPayload());
        String type = object.getString("type");
        //채팅 알림, 메세지 수신용 웹소켓에 회원 등록
        if (type != null && type.equals("chatRoom"))//1. 채팅방 입장시 회원 등록
        {
            String user = object.getString("userNo");
            String chatRoom = user + "Room";
            int mNo = chatUserService.selectMNobyUserNo(Integer.parseInt(user));
            List<ChatUser> chatUserList = chatUserService.selectChatUserListByMNo(mNo);
            for (ChatUser chatUser : chatUserList) {
                String userNoList = chatUser.getUserNo() + "List";
                if (userMap.containsKey(userNoList)) { // 해당유저의 위치는 room에 있으니 list로 등록된것은 삭제
                    userMap.remove(userNoList);
                }
            }
            String shakeList = user+"Shake";
            if(userMap.containsKey(shakeList)){
                userMap.remove(shakeList);
            }
            // highFiveList page 생기면 그것도 삭제할것
            String highFiveUser = mNo+"high";
            if(userMap.containsKey(highFiveUser)){
                userMap.remove(highFiveUser);
            }
            userMap.put(chatRoom, session);


        } else if (type.equals("chatList"))//2.채팅 리스트 진입시 mNo으로 userNo(들) 가져와서 등록
        {
            String tempMNo = object.getString("mNo");
            int mNo = Integer.parseInt(tempMNo);
            List<ChatUser> chatUserList = chatUserService.selectChatUserListByMNo(mNo);
            String stringMNo = mNo + "mNo";
            if(chatUserList.isEmpty()){ // chat user가 하나도 없는 경우
                userMap.put(stringMNo, session);

            }else{
                for (ChatUser chatUser : chatUserList) {
                    String userNo = chatUser.getUserNo() + "List";
                    userMap.put(userNo, session);

                    String chatRoom = chatUser.getUserNo() + "Room";
                    if (userMap.containsKey(chatRoom)) { // 해당유저의 위치는 List에 있으니 room으로 등록된것은 삭제
                        userMap.remove(chatRoom);
                    }
                }
                if(userMap.containsKey(stringMNo)){
                    userMap.remove(stringMNo);
                }
            }
            String shakeList = mNo+"Shake";
            if(userMap.containsKey(shakeList)){
                userMap.remove(shakeList);
            }
            // highFiveList page 생기면 그것도 삭제할것
            String highFiveUser = mNo+"high";
            if(userMap.containsKey(highFiveUser)){
                userMap.remove(highFiveUser);
            }

        } else if (type.equals("chatHeader"))//3. 페이지 헤더에 채팅 전체 알림 수신용 회원 등록
        {
            String user = object.getString("mNo");
            String headerUser = user + "Header";
            userMap.put(headerUser, session);

        } else if(type.equals("shakeList")){
            String user = object.getString("mNo");
            String shakeUser = user + "Shake";

            int mNo = Integer.parseInt(user);
            List<ChatUser> chatUserList = chatUserService.selectChatUserListByMNo(mNo);
            for (ChatUser chatUser : chatUserList) {
                String userNo = chatUser.getUserNo() + "List";
                String chatRoom = chatUser.getUserNo() + "Room";
                if (userMap.containsKey(chatRoom)) {
                    userMap.remove(chatRoom);
                }
                if (userMap.containsKey(userNo)) {
                    userMap.remove(userNo);
                }
            }
            // highFiveList page 생기면 그것도 삭제할것
            String highFiveUser = mNo+"high";
            if(userMap.containsKey(highFiveUser)){
                userMap.remove(highFiveUser);
            }
            userMap.put(shakeUser, session);


        } else if(type.equals("highFiveList")){
            String user = object.getString("mNo");
            String highFiveUser = user + "high";
            int mNo = Integer.parseInt(user);
            List<ChatUser> chatUserList = chatUserService.selectChatUserListByMNo(mNo);
            for (ChatUser chatUser : chatUserList) {
                String userNo = chatUser.getUserNo() + "List";
                String chatRoom = chatUser.getUserNo() + "Room";
                if (userMap.containsKey(chatRoom)) {
                    userMap.remove(chatRoom);
                }
                if (userMap.containsKey(userNo)) {
                    userMap.remove(userNo);
                }
            }
            String shakeList = mNo+"Shake";
            if(userMap.containsKey(shakeList)){
                userMap.remove(shakeList);
            }
            userMap.put(highFiveUser, session);

        } else if(type.equals("chatHeaderClear")) { // chatRoom 이나 list에 있지 않은 유저
            String tempMNo = object.getString("mNo");
            String headerUser = tempMNo + "Header";

            int mNo = Integer.parseInt(tempMNo);
            List<ChatUser> chatUserList = chatUserService.selectChatUserListByMNo(mNo);
            for (ChatUser chatUser : chatUserList) {
                String userNo = chatUser.getUserNo() + "List";
                String chatRoom = chatUser.getUserNo() + "Room";

                if (userMap.containsKey(chatRoom)) {
                    userMap.remove(chatRoom);
                }
                if (userMap.containsKey(userNo)) {
                    userMap.remove(userNo);
                }

            }
            String noChatUser = mNo + "mNo";
            if (userMap.containsKey(noChatUser)) {
                userMap.remove(noChatUser);
            }
            String shakeList = mNo+"Shake";
            if(userMap.containsKey(shakeList)){
                userMap.remove(shakeList);
            }
            String highFiveUser = mNo + "high";
            if(userMap.containsKey(highFiveUser)){
                userMap.remove(highFiveUser);
            }
            // highFiveList page 생기면 그것도 삭제할것

            userMap.put(headerUser, session);

        }
        //등록후 알림, 최근 메세지등 처음 수신
        else if (type.equals("enter"))//입장
        {
            //enter type3가지로 구분
            String enterType = object.optString("enterType");

            // 메세지를 안읽은 사람이 입장시 현재 메세지들 readCnt refresh 해주기
            if (enterType.equals("chatRoom"))//채팅방에 입장시 읽은메세지,읽지 않은 메시지 수신
            {
                String user = object.getString("userNo");
                int userNo = Integer.parseInt(user);
                user += "Room";
                WebSocketSession ws = (WebSocketSession) userMap.get(user);
                ChatUser chatUser = chatUserService.selectByUserNo(userNo);
                int unreadCnt = chatHistoryService.getUnreadCnt(chatUser);

                //채팅방 유저들의 정보 list에 담기
                List<ChatUser> userList = setChatUsersWithProfileAndNickCode(chatUser.getCrNo());
                ChatHistory chatHistory = new ChatHistory();
                chatHistory.setCrNo(chatUser.getCrNo());
                chatHistory.setChNo(chatUser.getChNo());
                chatHistory.setJoinPoint(chatUser.getJoinPoint());
                if (unreadCnt == 0) { // 모든 메세지를 읽은 경우

                    // 안읽은 메세지가 없으면 최근 10개의 메세지만 가져오기
                    List<ChatHistory> chatHistoryList = chatHistoryService.selectLast10Msg(chatHistory);
                    String last10Msg = chatService.setMsgList(chatHistoryList, userList, chatUser.getUserNo());
                    ws.sendMessage(new TextMessage(last10Msg));//읽은 채팅기록 보내기

                } else {

                    // 안읽은 메세지가 있는 경우 읽은 메세지 위로 10개 가져오고 안읽은 메세지 붙여 보내주기
                    List<ChatHistory> chatHistoryReadList = chatHistoryService.selectReadMsg(chatHistory);
                    String msg = chatService.setMsgList(chatHistoryReadList, userList, chatUser.getUserNo());
                    msg += "<div class=\"breadcrumb mt-2 mb-2\" id=\"unreadPoint\"><div class=\"breadcrumb-item active\">여기까지 읽으셨습니다.</div></div>";
                    // 안읽은 메세지들 읽음 처리하기
                    ChatHistory updateChatHistory = new ChatHistory();
                    updateChatHistory.setCrNo(chatUser.getCrNo());
                    updateChatHistory.setLimit(chatUser.getChNo()); // 마지막으로 읽은 메세지 지점
                    int maxChNo = chatHistoryService.selectMaxChNo(chatUser.getCrNo());
                    updateChatHistory.setChNo(maxChNo); // 해당 채팅방의 가장 최신글 번호
                    chatHistoryService.updateReadCnt(updateChatHistory); // 안읽은 메세지(들) 읽었으니 read count -1
                    // 해당 chatUser의 마지막으로 본 chNo 변경
                    ChatUser updateChatUser = chatUserService.selectByUserNo(userNo);
                    updateChatUser.setChNo(maxChNo);
                    chatUserService.updateChNo(updateChatUser);

                    // 안 읽은 메세지 전부 담기
                    chatHistory.setLimit(unreadCnt);
                    chatHistory.setJoinPoint(updateChatUser.getJoinPoint());
                    List<ChatHistory> chatHistoryUnreadList = chatHistoryService.selectUnreadMsg(chatHistory);
                    msg += chatService.setMsgList(chatHistoryUnreadList, userList, chatUser.getUserNo());
                    // 합쳐진 메세지 보내기
                    ws.sendMessage(new TextMessage(msg));

                    ws = (WebSocketSession) userMap.get(chatUser.getMNo()+"Header");
                    System.out.println("unreadCnt = " + unreadCnt);
                    System.out.println("<div class=\"discountUnreadCount\" data=\""+unreadCnt+"\"></div>");
                    ws.sendMessage(new TextMessage("<div class=\"discountUnreadCount\" data=\""+unreadCnt+"\"></div>"));

                    //채팅방에 있는 사람들은 실시간으로 readCount 변화 보여주기
                    for(ChatUser userAtRoom : userList){ // userList에는 모두 담겨있다. 방금 읽은 사람을 제외해야함
                        if(userAtRoom.getUserNo() != userNo){
                            if(userMap.containsKey(userAtRoom.getUserNo()+"Room")){
                                ws = (WebSocketSession) userMap.get(userAtRoom.getUserNo()+"Room");
                                msg = "<div class=\"someoneReadMsg\" data=\"";
                                System.out.println("chatHistoryUnreadList = " + chatHistoryUnreadList);
                                for(int i=0; i<chatHistoryUnreadList.size(); i++){
                                    msg += chatHistoryUnreadList.get(i).getChNo();
                                    if(i != chatHistoryUnreadList.size()-1){
                                        msg += "_";
                                    }
                                }

                                msg += "\" > </div>";
                                System.out.println("msg = " + msg);
                                //어디서부터 어디까지 읽었는지 data에 보내주기
                                ws.sendMessage(new TextMessage(msg));
                            }
                        }
                    }
                }

            } else if (enterType.equals("chatList"))//채팅방 리스트 접근시 최근 메세지 ,채팅방별 알림 수신
            {
                String tempMNo = object.getString("mNo");
                int mNo = Integer.parseInt(tempMNo);

                // 입장인지 알림인지 나누기위해 alertType 사용
                String alertType = object.getString("alertType");
                // 채팅방 리스트 입장시
                if (alertType.equals("enterList")) {
//                    List<ChatUser> myList = chatUserService.selectChatUserListByMNo(mNo);
                    List<ChatUser> myList = chatUserService.selectChatUserListByMNoSortTime(mNo);
                    String chatRoomShape = "";
                    for (ChatUser chatUser : myList) { // 해당 member가 참가한 userList
                        String user = chatUser.getUserNo() + "List";
                        WebSocketSession ws = (WebSocketSession) userMap.get(user);
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
                                    chatRoomSet.setRoomTitle(otherUser.getNick() + otherUser.getCode() + "님과의 대화");
                                }
                            }

                        }

                        chatRoomShape = chatService.setChatRoom(chatUserList, chatRoomSet);
                        chatRoomShape += "<div data=\"chatRoomList\"></div>";

                        ws.sendMessage(new TextMessage(chatRoomShape));

                    }

                }

                // 단체 채팅방의 경우 채팅방 이름은 최초 모집시 게시글 제목, 변경가능
                // 1:1 채팅방의 경우 상대방의 닉네임


            } else if (enterType.equals("chatHeader")) //사이트 이용시 전체 채팅 알림 수신
            {
                String user = object.getString("mNo");
                WebSocketSession ws = (WebSocketSession) userMap.get(user + "Header");
                int mNo = Integer.parseInt(user);
                // 내가 받은 요청 수
                int requestCount = chatService.getReceivedChatRequest(mNo);
                // 안읽은 메세지 수
                List<ChatUser> myChatUserList = chatUserService.selectChatUserListByMNo(mNo);
                int unreadCount = chatHistoryService.getAllUnreadCount(myChatUserList);
                int chatCount = requestCount+unreadCount;
                int shakeCount = shakerService.countShakeRequestByMNo(mNo);
                int highFiveCount = chatService.countHighFiveRequestByMNo(mNo);
                String msg = "<div class=\"chatAlert\" data=\"" + chatCount + "\">";
                msg += "<div class=\"shakeAlert\" data=\""+shakeCount+"\"></div>";
                msg += "<div class=\"highFiveAlert\" data=\""+highFiveCount+"\"></div>";

                msg += "</div>";
                ws.sendMessage(new TextMessage(msg));

            }
        } else if (type.equals("pastMsg")) { // 1:1 채팅방 불러오기
            // 지난 메세지 10개 가져오기
            String user = object.getString("userNo");
            int userNo = Integer.parseInt(user);

            user += "Room";
            WebSocketSession ws = (WebSocketSession) userMap.get(user);
            int crNo = Integer.parseInt(object.getString("chatRoomNo"));

            int lastChatNo = 0;
            try {
                // 반복적으로 메세지를 불러오다 끝에 걸리면 숫자가아닌게 들어온다.
                lastChatNo = Integer.parseInt(object.getString("lastChatNo"));

            } catch (NumberFormatException e) {
                lastChatNo = 0;
            } catch (JSONException e) {
                lastChatNo = 0;
            }
            if (lastChatNo != 0) {
                ChatHistory chatHistory = new ChatHistory();
                chatHistory.setCrNo(crNo);
                chatHistory.setChNo(lastChatNo);
                ChatUser chatUser = chatUserService.selectByUserNo(userNo);
                chatHistory.setJoinPoint(chatUser.getJoinPoint());
                //채팅방 유저들의 정보 list에 담기
                List<ChatUser> userList = setChatUsersWithProfileAndNickCode(crNo);

                // 10개만 가져와서 붙여준다.
                List<ChatHistory> chatHistoryList = chatHistoryService.selectPast10Msg(chatHistory);

                String past10Msg = chatService.setMsgList(chatHistoryList, userList, userNo);
                past10Msg += "<div class=\"pastMsg\"></div>";
                ws.sendMessage(new TextMessage(past10Msg));//읽은 채팅기록 보내기
            }


        }
        //메세지 전송 모듈
        else if (type.equals("chat")) {
            //1.채팅룸 기준으로 셀렉
            //2.users 리스트 3번반복하며 해당유저에게 메세지 전송
            String crNo = object.getString("chatRoomNo");//채팅방 번호
            String userNo = object.getString("userNo");//보낸사람
            String content = object.getString("message");//메세지 내용

            //채팅방에 혼자 있는 경우 메세지 작성 안되게 하기
            int usercnt = chatUserService.getCountAtChatRoom(Integer.parseInt(crNo));
            if(usercnt == 1){
                WebSocketSession ws = (WebSocketSession) userMap.get(userNo + "Room");
                ws.sendMessage(new TextMessage("ALONE_AT_ROOM"));
            }else{
                List<ChatUser> chatUsers = setChatUsersWithProfileAndNickCodeOnlyNUser(Integer.parseInt(crNo));

                int readCount = chatUserService.getCountAtChatRoom(Integer.parseInt(crNo));
                ChatHistory chatHistory = setChatHistory(crNo, content, userNo, readCount);

                int senderNo = chatHistory.getUserNo();
                //보낸 사람 정보 set 하기
                ChatUser sender = new ChatUser();
                for (ChatUser chatUser : chatUsers) {
                    if (senderNo == chatUser.getUserNo()) {
                        sender = chatUser;
                    }
                }
                // 현재 채팅방에 있는 사람은 메세지를 바로 보기때문에 읽음 처리해주기
                int peopleAtRoom = readCount;
                for (ChatUser chatUser : chatUsers) {
                    String chatRoom = chatUser.getUserNo() + "Room";
                    if (users.contains(userMap.get(chatRoom))) {
                        peopleAtRoom--;
                    }
                }
                chatHistory.setReadCount(peopleAtRoom);

                // 메세지 insert
                try {
                    chatHistory = chatHistoryService.insertMessage(chatHistory);
                    chatRoomService.updateTimeAtChatRoom(chatHistory.getCrNo());
                } catch (Exception e) {

                    WebSocketSession ws = (WebSocketSession) userMap.get(userNo + "Room");
                    ws.sendMessage(new TextMessage("MSG_SEND_ERR"));
                }

                for (ChatUser chatUser : chatUsers) {
                    String chatRoom = chatUser.getUserNo() + "Room";
                    String chatList = chatUser.getUserNo() + "List";
                    String chatHeader = chatUser.getMNo() + "Header";

                    if (users.contains(userMap.get(chatRoom))) { //채팅방에 있는 사람들에게 메세지 전송
                        // 보낸 사람과 받는 사람이 같다면 말풍선 다르게 바꿔주기
                        String msg = "";
                        if (senderNo == chatUser.getUserNo()) {
                            msg = chatService.setSenderMsg(chatHistory);
                        } else { // 보낸 사람과 받는 사람이 다르다면 프로필 사진 nick 보여주기
                            msg = chatService.setReceivedMsg(chatHistory, sender);
                        }

                        WebSocketSession ws = (WebSocketSession) userMap.get(chatRoom);
                        //읽은 chatUser들 chNo update
                        chatUser.setChNo(chatHistory.getChNo());
                        chatUserService.updateChNo(chatUser);
                        ws.sendMessage(new TextMessage(msg));

                    } else if (users.contains(userMap.get(chatList))) {
                        String chatRoomShape = "";
                        int receiverCrNo = chatUser.getCrNo();
                        int listUserNo = chatUser.getUserNo();
                        int unreadCnt = chatHistoryService.getUnreadCnt(chatUser);

                        ChatRoom receiveChatRoom = chatRoomService.selectChatRoomByCrNo(receiverCrNo);

                        // 해당 member를 포함한 같은 채팅방에 있는 유저 리스트
                        List<ChatUser> chatUserList = setChatUsersWithProfileAndNickCode(receiverCrNo);

                        // 전달용 model
                        ChatRoomSet chatRoomSet = new ChatRoomSet();
                        chatRoomSet.setMyUserNo(listUserNo);
                        chatRoomSet.setUnreadCnt(unreadCnt);


                        // 변경된 채팅방 가져와서 삭제시키고 붙여넣기
                        // 마지막 메세지가 시스템 메세지면 lastChat에 아무것도 안들어옴
                        // 마지막 메세지의 userNo 이 0이면 따로 설정해야함
                        int userNoChecker = chatHistoryService.selectLastMsgSendUserNo(receiverCrNo);
                        if (userNoChecker == 0) { // 시스템 메세지
                            chatRoomSet.setLastNick("system");
                            String systemMsg = chatHistoryService.selectSystemMsgByCrNo(receiverCrNo);
                            chatRoomSet.setLastMsg(systemMsg);
                        } else {
                            // 마지막 메세지, nick set
                            ChatHistory lastChat = chatHistoryService.selectLastChatAtChatRoom(receiverCrNo);
                            chatRoomSet.setLastNick(lastChat.getNick());
                            chatRoomSet.setLastMsg(lastChat.getMessage());
                        }

                        if (receiveChatRoom.getGroupChat().equals("y")) { // 단체 채팅방
                            // 채팅방 이름 불러오기
                            chatRoomSet.setRoomTitle(receiveChatRoom.getCrTitle());

                        } else { // 1:1 채팅방
                            for (ChatUser otherUser : chatUserList) { // 상대방 닉네임 가져오기
                                if (otherUser.getUserNo() != chatUser.getUserNo()) {
                                    chatRoomSet.setRoomTitle(otherUser.getNick() + otherUser.getCode() + "님과 채팅");
                                }
                            }
                        }

                        chatRoomShape = chatService.setChatRoom(chatUserList, chatRoomSet);
                        chatRoomShape += "<div class=\"chatRoomListAlert\"></div>";
                        WebSocketSession ws = (WebSocketSession) userMap.get(chatList);

                        ws.sendMessage(new TextMessage("<div id=\"chatRoomListAlert_"+ receiverCrNo +"\"></div>"));
                        ws.sendMessage(new TextMessage(chatRoomShape));

                        // header alert 보내기
                        ws = (WebSocketSession) userMap.get(chatUser.getMNo()+"Header");
                        ws.sendMessage(new TextMessage("<div class=\"chatReceived\"></div>"));


                    } else if (users.contains(userMap.get(chatHeader))) {

                        WebSocketSession ws = (WebSocketSession) userMap.get(chatHeader);
                        ws.sendMessage(new TextMessage("<div class=\"chatReceived\"></div>"));
                    }
                }
            }


        } else if (type.equals("leaveChat")) { // 채팅방 떠날 때
            int crNo = Integer.parseInt(object.getString("chatRoomNo"));
            int userNo = Integer.parseInt(object.getString("userNo"));

            List<ChatUser> chatUsers = setChatUsersWithProfileAndNickCode(crNo); // 채팅방에 있는 사람들(나가는사람 포함)
            int readCnt = chatUserService.getCountAtChatRoom(crNo);
            for (ChatUser chatUser : chatUsers) { // 나간 메세지 readCnt
                String chatRoom = chatUser.getUserNo() + "Room";
                if (users.contains(userMap.get(chatRoom))) {
                    readCnt--;
                }
            }

            ChatHistory chatHistory = new ChatHistory(); // 나간 메세지 set용
            int errCnt = 0;
            for (ChatUser chatUser : chatUsers) {
                if (chatUser.getUserNo() == userNo && chatUser.getCrNo() == crNo) { // 나가는 사람일 경우
                    try {
                        String nick = chatUserService.getUserNick(chatUser.getUserNo());
                        chatUser.setNick(nick);
                        chatUser.setJoinPoint(readCnt); // joinPoint에 readCnt 담아 보내기
                        chatHistory = chatUserService.leaveChatRoomTransaction(chatUser); //tx 처리

                    } catch (Exception e) {
                        e.printStackTrace();
                        WebSocketSession ws = (WebSocketSession) userMap.get(chatUser.getUserNo() + "Room");
                        ws.sendMessage(new TextMessage("LEAVE_ERR"));
                        errCnt++;
                    }
                }
            }
            if (errCnt == 0) {
                chatHistory = chatHistoryService.selectMsgByChNo(chatHistory.getChNo());
                ChatRoomSet chatRoomSet = new ChatRoomSet();
                ChatRoom receiveChatRoom = chatRoomService.selectChatRoomByCrNo(crNo);

                for (ChatUser chatUser : chatUsers) {
                    String chatRoom = chatUser.getUserNo() + "Room";
                    String chatList = chatUser.getUserNo() + "List";
                    ChatRoom chatRoomModel = chatRoomService.selectChatRoomByCrNo(crNo);
                    if(chatRoomModel.getGroupChat().equals("n")){

                    }
                    if (users.contains(userMap.get(chatRoom))) { //채팅방에 있는 사람들에게 메세지 전송
                        WebSocketSession ws = (WebSocketSession) userMap.get(chatUser.getUserNo() + "Room");
                        if (chatUser.getUserNo() == userNo && chatUser.getCrNo() == crNo) { // 나가는 사람일 경우
                            ws.sendMessage(new TextMessage("LEAVE_SUCCESS"));
                            userMap.remove(chatRoom); // userMap에서 삭제
                        } else { // 나가는사람을 제외한 채팅방에 있는 사람들
                            String msg = chatService.setSystemMsg(chatHistory);
                            ws.sendMessage(new TextMessage(msg));
                        }
                    } else if (users.contains(userMap.get(chatList))) { // 채팅 리스트에 있는 경우
                        // 누구님이 나가셨습니다만 보여주기
                        String chatRoomShape = "";
                        // 전달용 model
                        chatRoomSet.setMyUserNo(chatUser.getUserNo());
                        int unreadCnt = chatHistoryService.getUnreadCnt(chatUser);
                        chatRoomSet.setUnreadCnt(unreadCnt);
                        chatRoomSet.setLastNick("system");
                        chatRoomSet.setLastMsg(chatHistory.getMessage());
                        List<ChatUser> chatUserList = setChatUsersWithProfileAndNickCode(crNo);
                        if (receiveChatRoom.getGroupChat().equals("y")) { // 단체 채팅방
                            // 채팅방 이름 불러오기
                            chatRoomSet.setRoomTitle(receiveChatRoom.getCrTitle());
                        } else { // 1:1 채팅방
                            for (ChatUser otherUser : chatUserList) { // 상대방 닉네임 가져오기
                                if (otherUser.getUserNo() != chatUser.getUserNo()) {
                                    chatRoomSet.setRoomTitle(otherUser.getNick() + otherUser.getCode() + "님과 채팅");
                                }
                            }
                        }

                        chatRoomShape = chatService.setChatRoom(chatUserList, chatRoomSet);
                        chatRoomShape += "<div class=\"chatRoomListAlert\"></div>";
                        WebSocketSession ws = (WebSocketSession) userMap.get(chatList);
                        ws.sendMessage(new TextMessage("<div id=\"chatRoomListAlert_" + crNo + "\"></div>"));
                        ws.sendMessage(new TextMessage(chatRoomShape));

                    }
                }

            }


        } else if (type.equals("chatRoomCreated")) {

            // 채팅 수락하여 채팅방 붙여주기
            int mNo = Integer.parseInt(object.getString("mNo"));
            int crNo = Integer.parseInt(object.getString("crNo"));
            int myUserNo = 0;
            List<ChatUser> myChatUserList = chatUserService.selectChatUserListByMNo(mNo);
            List<ChatUser> chatUsers = setChatUsersWithProfileAndNickCode(crNo); // chatRoom에 있는사람들
            for (ChatUser myChatUser : myChatUserList) {
                for (ChatUser atChatRoom : chatUsers) {
                    if (atChatRoom.getCrNo() == myChatUser.getCrNo())
                        myUserNo = myChatUser.getUserNo();
                }
            }

            for (ChatUser chatUser : chatUsers) {
                String chatList = chatUser.getUserNo() + "List";

                if (users.contains(userMap.get(chatList))) { // chat list에 있는 경우

                    // 전달용 model
                    ChatRoomSet chatRoomSet = new ChatRoomSet();
                    chatRoomSet.setMyUserNo(myUserNo);
                    chatRoomSet.setUnreadCnt(0);
                    chatRoomSet.setLastNick("system");

                    String systemMsg = chatHistoryService.selectSystemMsgByCrNo(crNo);
                    chatRoomSet.setLastMsg(systemMsg);

                    for (ChatUser otherUser : chatUsers) { // 상대방 닉네임 가져오기
                        if (otherUser.getUserNo() != myUserNo) {
                            chatRoomSet.setRoomTitle(otherUser.getNick() + otherUser.getCode() + "님과 채팅");
                        }
                    }
                    String chatRoomShape = "";
                    WebSocketSession ws = (WebSocketSession) userMap.get(chatList);
                    chatRoomShape = chatService.setChatRoom(chatUsers, chatRoomSet);
                    chatRoomShape += "<div class=\"chatRoomCreated\"></div>";


                    ws.sendMessage(new TextMessage(chatRoomShape));
                }

            }


        } else if (type.equals("chatRequestAccepted")) { // 채팅 수락시 생성된 userNo으로 session 등록 해주기
            int mNo = Integer.parseInt(object.getString("mNo"));
            int crNo = Integer.parseInt(object.getString("crNo"));
            List<ChatUser> myChatUserList = chatUserService.selectChatUserListByMNo(mNo);
            ChatUser myChatUser = new ChatUser();
            for (ChatUser chatUser : myChatUserList) {
                if (chatUser.getCrNo() == crNo) {
                    myChatUser = chatUser;
                    //요청 받으면서 생성된 내 userNo 으로 session에 등록
                    String userNo = chatUser.getUserNo() + "List";
                    userMap.put(userNo, session);
                }
            }
            List<ChatUser> chatUsers = setChatUsersWithProfileAndNickCode(crNo); // 같은 chatRoom에 있는사람들

            for (ChatUser chatUserAtRoom : chatUsers) {
                if (chatUserAtRoom.getUserNo() != myChatUser.getUserNo()) { // 요청을 보낸 사람이면
                    List<ChatUser> otherChatUserList = chatUserService.selectChatUserListByMNo(chatUserAtRoom.getMNo());
                    for (ChatUser otherChatUser : otherChatUserList) {
                        String chatList = otherChatUser.getUserNo() + "List";
                        if (userMap.containsKey(chatList)) { // chat list 페이지에 있는 경우
                            // 상대방의 session을 등록
                            WebSocketSession ws = (WebSocketSession) userMap.get(chatList);
                            userMap.put(chatUserAtRoom.getUserNo() + "List", ws);
                            break;
                        }
                    }
                }
            }


        } else if (type.equals("sendChatRequest")) {
            // 채팅 요청을 보냈을 때
            int sender = Integer.parseInt(object.getString("sender"));
            int receiver = Integer.parseInt(object.getString("receiver"));
            ChatRequest chatRequest = new ChatRequest();
            chatRequest.setSender(sender);
            chatRequest.setReceiver(receiver);
            chatRequest = chatService.selectChatRequestBySenderAndReceiver(chatRequest);

            Member senderInfo = memberService.selectByMno(sender);
            String[] nickAndCode = memberService.separateNick(senderInfo.getNick());
            chatRequest.setPhoto(memberService.selectPhotoByMno(sender));
            chatRequest.setNick(nickAndCode[0]);
            chatRequest.setCode(nickAndCode[1]);

            List<ChatUser> receiverUserList = chatUserService.selectChatUserListByMNo(receiver);
            if(receiverUserList.isEmpty()){
                String stringMNo = receiver + "mNo";
                String header = receiver + "Header";

                if(users.contains(userMap.get(stringMNo))){ // chatUser가없는 유저가 list에 있는 경우

                    String msg = chatService.setChatRequest(chatRequest);
                    msg += "<div class=\"receivedChatRequest\"></div>";
                    WebSocketSession ws = (WebSocketSession) userMap.get(stringMNo);
                    ws.sendMessage(new TextMessage(msg));

                    ws = (WebSocketSession) userMap.get(header);
                    msg = "<div class=\"receivedChatRequestAlert\"></div>";
                    ws.sendMessage(new TextMessage(msg));
                }else if(users.contains(userMap.get(header))){
                    String msg = "<div class=\"receivedChatRequestAlert\"></div>";
                    WebSocketSession ws = (WebSocketSession) userMap.get(header);
                    ws.sendMessage(new TextMessage(msg));
                }
            }else{
                for(ChatUser chatUser : receiverUserList){
                    String chatList = chatUser.getUserNo()+"List";
                    String header = chatUser.getMNo()+"Header";
                    if(users.contains(userMap.get(chatList))){ // 받는 사람이 chat List에 있는 경우 새로운 창 띄워주기
                        String msg = chatService.setChatRequest(chatRequest);
                        msg += "<div class=\"receivedChatRequest\"></div>";
                        WebSocketSession ws = (WebSocketSession) userMap.get(chatList);
                        ws.sendMessage(new TextMessage(msg));

                        ws = (WebSocketSession) userMap.get(header);
                        msg = "<div class=\"receivedChatRequestAlert\"></div>";
                        ws.sendMessage(new TextMessage(msg));

                        break;
                    }else if(users.contains(userMap.get(header))){ // 받는 사람이 list room 제외한 곳에 있을 때

                        String msg = "<div class=\"receivedChatRequestAlert\"></div>";
                        WebSocketSession ws = (WebSocketSession) userMap.get(header);
                        ws.sendMessage(new TextMessage(msg));
                    }
                }
            }



        }else if(type.equals("responseRequest")){
            // 수락, 거절을 한 경우 header 에서 알람 카운트 줄여주기
            String header = object.getString("mNo")+"Header";
            if(users.contains(userMap.get(header))){ // 유저가 접속해 있는 상태일 때
                String msg = "<div class=\"responseRequest\"></div>";
                WebSocketSession ws = (WebSocketSession) userMap.get(header);
                ws.sendMessage(new TextMessage(msg));
            }

        }else if(type.equals("SendShakeRequest")){
            int receiver = Integer.parseInt(object.getString("receiver"));
            int sender = Integer.parseInt(object.getString("sender"));
            ShakeRequest shakeRequest = new ShakeRequest();
            shakeRequest.setReceiver(receiver);
            shakeRequest.setSender(sender);

            shakeRequest = shakerService.selectRequestBySenderAndReceiver(shakeRequest);

            String shakeUser = receiver + "Shake";
            if(users.contains(userMap.get(shakeUser))){
                Member senderInfo = memberService.selectByMno(sender);
                String photo = memberService.selectPhotoByMno(sender);
                String[] nickAndCode = memberService.separateNick(senderInfo.getNick());
                shakeRequest.setNick(nickAndCode[0]);
                shakeRequest.setCode(nickAndCode[1]);
                shakeRequest.setPhoto(photo);

                String msg = chatService.setShakeRequest(shakeRequest);
                msg += "<div class=\"receivedShakerRequestAtList\"></div>";
                WebSocketSession ws = (WebSocketSession) userMap.get(shakeUser);
                ws.sendMessage(new TextMessage(msg));
            }

            String header = receiver+"Header";
            if(users.contains(userMap.get(header))){
                String msg = "<div class=\"receivedShakerRequest\"></div>";
                WebSocketSession ws = (WebSocketSession) userMap.get(header);
                ws.sendMessage(new TextMessage(msg));
            }

        }else if(type.equals("responseShakeRequest")){
            String header = object.getString("mNo") + "Header";
            if(users.contains(userMap.get(header))){
                String msg = "<div class=\"responseShakerRequest\"></div>";
                WebSocketSession ws = (WebSocketSession) userMap.get(header);
                ws.sendMessage(new TextMessage(msg));
            }

        }else if(type.equals("sendHighFiveRequest")){
            int sender = Integer.parseInt(object.getString("sender"));
            int crNo = Integer.parseInt(object.getString("crNo"));
            int receiver = chatRoomService.selectAdmin(crNo);

            String header = receiver+"Header";
            if(users.contains(userMap.get(header))){
                String msg = "<div class=\"receivedHighFiveRequest\"></div>";
                WebSocketSession ws = (WebSocketSession) userMap.get(header);
                ws.sendMessage(new TextMessage(msg));
            }

            String highFiverUser = receiver+"high";
            if(users.contains(userMap.get(highFiverUser))){
                HighFiveRequest highFiveRequest = chatService.selectHighFiveRequestBySenderAndCrNo(sender, crNo);
                ChatRoom chatRoom = chatRoomService.selectChatRoomByCrNo(crNo);
                highFiveRequest.setTitle(chatRoom.getCrTitle());
                highFiveRequest.setPhoto(memberService.selectPhotoByMno(sender));
                Member member = memberService.selectByMno(sender);
                String[] nickAndCode = memberService.separateNick(member.getNick());
                highFiveRequest.setNick(nickAndCode[0]);
                highFiveRequest.setCode(nickAndCode[1]);
                String msg = "<div class=\"receivedHighFive\"></div>";
                msg += chatService.setHighFiveRequest(highFiveRequest);
                WebSocketSession ws = (WebSocketSession) userMap.get(highFiverUser);
                ws.sendMessage(new TextMessage(msg));
            }

        }else if(type.equals("responseHighFive")){
            String header = object.getString("mNo")+"Header";
            if(users.contains(userMap.get(header))){ // 유저가 접속해 있는 상태일 때
                String msg = "<div class=\"responseHighFive\"></div>";
                WebSocketSession ws = (WebSocketSession) userMap.get(header);
                ws.sendMessage(new TextMessage(msg));
            }
        }else if(type.equals("kickUserOut")){
            int targetNo = Integer.parseInt(object.getString("target"));
            ChatUser target = chatUserService.selectByUserNoWithOutStatus(targetNo);
            int crNo = target.getCrNo();
            List<ChatUser> chatUserList = chatUserService.selectByCrNo(crNo);
            ChatRoom chatRoom = chatRoomService.selectChatRoomByCrNo(crNo);
            int king = chatRoom.getAdmin();
            String msg = "";
            WebSocketSession ws;
            msg = "kickedOut";
            // 강퇴 당한사람이 list
            if(users.contains(userMap.get(targetNo+"List"))){
                ws = (WebSocketSession) userMap.get(targetNo+"List");
                ws.sendMessage(new TextMessage(msg));
            }else if(users.contains(userMap.get(targetNo+"Room"))){
                // chatroom에 있을 경우
                ws = (WebSocketSession) userMap.get(targetNo+"Room");
                ws.sendMessage(new TextMessage(msg));
            }
            System.out.println("chatUserList = " + chatUserList);
            for(ChatUser chatUser : chatUserList){ //그 외 user들
                if(king != chatUser.getMNo()){
                    System.out.println("chatUser = " + chatUser);
                    String userList = chatUser.getUserNo()+"List";
                    String userRoom = chatUser.getUserNo()+"Room";
                    msg = "someBodyKickedOut";
                    if(users.contains(userMap.get(userList))){ // list에 있는 유저
                        System.out.println("list에 있음");
                        ws = (WebSocketSession) userMap.get(userList);
                        ws.sendMessage(new TextMessage(msg));
                    }else if(users.contains(userMap.get(userRoom))){ // 채팅방에 있는 유저
                        System.out.println("room에 있음");
                        ws = (WebSocketSession) userMap.get(userRoom);
                        ws.sendMessage(new TextMessage(msg));
                    }
                }
            }

        }else if(type.equals("inheritKing")){

            int targetNo = Integer.parseInt(object.getString("target"));
            String msg = "inheritKing";
            WebSocketSession ws;
            if(users.contains(userMap.get(targetNo+"Room"))){
                // chatroom에 있을 경우
                ws = (WebSocketSession) userMap.get(targetNo+"Room");
                ws.sendMessage(new TextMessage(msg));
            }

        }else if(type.equals("giveKing")){
            int crNo = Integer.parseInt(object.getString("chatRoomNo"));
            int userNo = Integer.parseInt(object.getString("userNo"));

            ChatUser chatUser = chatUserService.selectOldestUser(crNo, userNo);
            chatUser = chatUserService.selectByUserNo(chatUser.getUserNo());
            int targetNo = chatUser.getUserNo();
            chatService.changeKing(chatUser);
            String msg = "inheritKing";
            WebSocketSession ws;
            List<ChatUser> chatUserList = chatUserService.selectByCrNo(crNo);

            for(ChatUser userAtRoom : chatUserList){
                if(users.contains(userMap.get(userAtRoom.getUserNo()+"Room"))){
                    // chatroom에 있을 경우
                    ws = (WebSocketSession) userMap.get(targetNo+"Room");
                    ws.sendMessage(new TextMessage(msg));
                }
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
    private List<ChatUser> setChatUsersWithProfileAndNickCodeOnlyNUser(int crNo) {
        List<ChatUser> userList = chatUserService.selectByCrNoNUsers(crNo);

        for (int i = 0; i < userList.size(); i++) { // 닉네임 분리작업
            //nick # 분리
            String[] nickAndCode = memberService.separateNick(userList.get(i).getNick());
            userList.get(i).setNick(nickAndCode[0]);
            userList.get(i).setCode(nickAndCode[1]);
        }
        return userList;
    }
    private ChatHistory setChatHistory(String crNo, String content, String userNo, int readCount) {
        ChatHistory chatHistory = new ChatHistory();
        chatHistory.setCrNo(Integer.parseInt(crNo));
        chatHistory.setMessage(content);
        chatHistory.setUserNo(Integer.parseInt(userNo));
        chatHistory.setReadCount(readCount);

        return chatHistory;
    }

    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        //연결종료
        users.remove(session);//웹세션에서 유저 삭제
        if(userMap.containsValue(session)){
            userMap.values().remove(session); // userMap에서 해당하는 값으로 삭제
        }
        if(userMap.containsValue(null)){
            userMap.values().remove(session); // userMap에서 해당하는 값으로 삭제
        }
        if(users.contains(null)){
            users.remove(null);
        }

    }

}
