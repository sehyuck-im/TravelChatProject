package com.TravelChat.chat.controller;

import com.TravelChat.board.service.BoardService;
import com.TravelChat.chat.model.ChatRequest;
import com.TravelChat.chat.model.ChatRoom;
import com.TravelChat.chat.model.ChatUser;
import com.TravelChat.chat.model.HighFiveRequest;
import com.TravelChat.chat.service.ChatHistoryService;
import com.TravelChat.chat.service.ChatRoomService;
import com.TravelChat.chat.service.ChatService;
import com.TravelChat.chat.service.ChatUserService;
import com.TravelChat.member.model.Member;
import com.TravelChat.member.model.ShakeRequest;
import com.TravelChat.member.service.MemberService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/chat")
public class ChatController {
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
    @Autowired
    private BoardService boardService;

    @GetMapping("/request")
    @ResponseBody
    public String requestChat(@RequestParam("receiver") String receiver) {
        int mNo = (int) session.getAttribute("mNo");
        Member member = ms.selectByMno(mNo);
        int sender = member.getMNo();
        int receiverMNo = Integer.parseInt(receiver);

        String result = "";

        boolean isChatRoom = false;
        List<ChatUser> myChatUserList = chatUserService.selectChatUserListByMNo(mNo);
        List<ChatUser> receiverUserList = chatUserService.selectChatUserListByMNo(receiverMNo);
        int crNo = 0;
        for(ChatUser myChatUser : myChatUserList){
            ChatRoom chatRoom = chatRoomService.selectChatRoomByCrNo(myChatUser.getCrNo());
            for(ChatUser receiverUser : receiverUserList){
                if(receiverUser.getCrNo()==myChatUser.getCrNo() && chatRoom.getGroupChat().equals("n")){
                    //둘다 참여 중이고 1:1 채팅방인 경우
                    isChatRoom = true;
                    crNo = receiverUser.getCrNo();

                }
            }
        }
        if(!isChatRoom){ // 채팅방이 없는 경우
            ChatRequest chatRequest = new ChatRequest();
            chatRequest.setSender(sender);
            chatRequest.setReceiver(receiverMNo);
            int cnt = chatService.selectChatRequestCnt(chatRequest);
            if (cnt == 0) {
                try {
                    chatService.createRequest(chatRequest);
                    ChatRequest tempRequest = chatService.selectChatRequestBySenderAndReceiver(chatRequest);
                    result = "SUCCESS"+tempRequest.getRequestNo();

                } catch (Exception e) {

                    result = "REQUEST_ERR";
                }

                return result;
            } else {
                return "WAIT_RESPONSE";
            }
        }else{ // 채팅이 있다면
            result = "crNo="+crNo;
            return result;
        }
    }

    @GetMapping("/list")
    public String chatList(Model model) {
        int mNo = (int) session.getAttribute("mNo");
        Member member = ms.selectByMno(mNo);
        // 받은 요청 리스트
        List<ChatRequest> tempRequestList = chatService.selectReceivedRequest(member.getMNo());
        List<ChatRequest> requestList = new ArrayList<>();

        // 받은요청 리스트에 보낸사람 프로필 정보 set 하기
        for (ChatRequest chatRequest : tempRequestList) {

            Member senderMember = ms.selectByMno(chatRequest.getSender());
            String senderPhoto = ms.selectPhotoByMno(chatRequest.getSender());
            String[] nickAndCode = ms.separateNick(senderMember.getNick());
            chatRequest.setCode(nickAndCode[1]);
            chatRequest.setNick(nickAndCode[0]);
            chatRequest.setPhoto(senderPhoto);
            requestList.add(chatRequest);
        }

        model.addAttribute("requestList", requestList);
        return "chat/list";
    }

    @GetMapping("/responseRequest")
    @ResponseBody
    public String responseRequest(@RequestParam("requestNo") int requestNo, @RequestParam("type") String type) {
        // 1. chatRequest 조회, null 일경우 0 반환
        int requestCount = chatService.getRequestCountByRequestNo(requestNo);

        // 2. 해당하는 data 있으면 동작
        if (requestCount != 0) {
            // type에 따라 수락인지 거절인지 분기
            ChatRequest chatRequest = chatService.selectChatRequest(requestNo);
            if (type.equals("accept")) { // 수락일 경우 채팅방 생성 후 요청 삭제
                try {
                    chatService.createChatRoomAndDeleteRequest(chatRequest);
                } catch (Exception e) {
                    e.printStackTrace();
                    // CHATUSER_INSERT_ERR, CHATREQ_DEL_ERR, CHATROOM_INSERT_ERR
                    if (e.getMessage().equals("CHATROOM_INSERT_ERR") || e.getMessage().equals("CHATREQ_DEL_ERR") ||
                            e.getMessage().equals("CHATUSER_INSERT_ERR")) {
                        return "RESPONSE_ERR";
                    } else {
                        return "UNEXPECTED_ERR";
                    }
                }
                int sender = chatRequest.getSender();
                int receiver = chatRequest.getReceiver();
                int crNo = 0;
                List<ChatUser> chatUserSenderList = chatUserService.selectChatUserListByMNo(sender);
                List<ChatUser> chatUserReceiverList = chatUserService.selectChatUserListByMNo(receiver);
                for(ChatUser sendChatUser : chatUserSenderList){
                    ChatRoom chatRoom = chatRoomService.selectChatRoomByCrNo(sendChatUser.getCrNo());
                    for(ChatUser receiveChatUser : chatUserReceiverList){
                        if(receiveChatUser.getCrNo()==sendChatUser.getCrNo() && chatRoom.getGroupChat().equals("n")){
                            //둘다 참여 중이고 1:1 채팅방인 경우
                            crNo = chatRoom.getCrNo();
                        }
                    }
                }

                return "ACCEPTED"+crNo;
            } else { // 거절일 경우 요청만 삭제

                try {
                    chatService.deleteRequest(chatRequest);
                } catch (Exception e) {
                    e.printStackTrace();
                    return "DELETE_ERR";
                }
                // 요청을 거절한 경우 알림 보내주기
                return "DECLINE";
            }
        } else {
            return "REQUEST_NOT_FOUND";
        }

    }

    @GetMapping("/chatRoom")
    public String chatRoom(String crNo, Model model){
        int mNo = (int) session.getAttribute("mNo");
        int ICrNo = Integer.parseInt(crNo);
        int cnt = chatUserService.countUserNoByMNoAndCrNo(mNo, ICrNo);

        if(cnt == 0){ // chatUser가 해당하는 crNo에 없다면
            model.addAttribute("result", -1);

            return "chat/chatRoomBack";
        }

        ChatRoom chatRoom = chatRoomService.selectChatRoomByCrNo(ICrNo);
        List<ChatUser> chatUserList = chatUserService.selectByCrNoNUsers(ICrNo);
        int userNo = chatUserService.selectUserNoByMNoAndCrNo(mNo, ICrNo);
        ChatUser chatUser = chatUserService.selectByUserNo(userNo); // 사용자

        if(chatRoom.getGroupChat().equals("y")){ // 단체 채팅방이면 채팅방 이름 set
            if(chatRoom.getCrTitle().equals("")){
                String title = boardService.selectBoardTitleByCrNo(ICrNo);
                chatRoom.setCrTitle(title);
            }
            for(ChatUser user : chatUserList){ // 닉네임 코드 분리
                String[] nickAndCode = ms.separateNick(user.getNick());
                user.setNick(nickAndCode[0]);
                user.setCode(nickAndCode[1]);
            }
            model.addAttribute("chatUserList", chatUserList);
            model.addAttribute("roomTitle", chatRoom.getCrTitle());

        }else{
            for(ChatUser temp : chatUserList){
                if(temp.getMNo() != mNo){ // 상대방 이름으로 room title set 해주기
                    model.addAttribute("roomTitle", temp.getNick()+"님과의 대화");
                    model.addAttribute("targetUserNo", temp.getUserNo());
                }
            }
        }
        model.addAttribute("chatRoom", chatRoom);
        model.addAttribute("chatUser", chatUser);

        return "chat/chatRoom";
    }
    @GetMapping("/highFiveRequest")
    @ResponseBody
    public String requestChat(@RequestParam("crNo") int crNo) {
        int mNo = (int) session.getAttribute("mNo");
        // 1. 요청한 사람이 이미 채팅방에 있는지 확인, 요청이 있는지도 확인
        int userCount = chatUserService.countUserNoByMNoAndCrNo(mNo, crNo);
        int requestCount = chatService.countHighFiveRequestByMNoAndCrNo(mNo, crNo);
        if(userCount>0 || requestCount > 0){
            return "CHECK";
        }
        // 2. 요청 보내기
        HighFiveRequest highFiveRequest = new HighFiveRequest();
        highFiveRequest.setCrNo(crNo);
        highFiveRequest.setSender(mNo);
        ChatRoom chatRoom = chatRoomService.selectChatRoomByCrNo(crNo);
        highFiveRequest.setReceiver(chatRoom.getAdmin());
        try {
            chatService.insertHighFiveRequest(highFiveRequest);

            return "REQ_OK";
        } catch (Exception e) {
            e.printStackTrace();
            return  "REQ_FAIL";
        }
    }

    @GetMapping("/highFiveList")
    public String highFiveList(Model model) {
        int mNo = (int) session.getAttribute("mNo");

        List<HighFiveRequest> highFiveRequestList = chatService.selectHighFiveRequestList(mNo);
        for(HighFiveRequest highFiveRequest : highFiveRequestList){
            Member temp = ms.selectByMno(highFiveRequest.getSender());
            String[] nickAndCode = ms.separateNick(temp.getNick());
            highFiveRequest.setNick(nickAndCode[0]);
            highFiveRequest.setCode(nickAndCode[1]);
            highFiveRequest.setPhoto(ms.selectPhotoByMno(highFiveRequest.getSender()));
            String title = boardService.selectBoardTitleByCrNo(highFiveRequest.getCrNo());
            highFiveRequest.setTitle(title);
        }
        model.addAttribute("mNo", mNo);
        model.addAttribute("highFiveRequestList", highFiveRequestList);
        return "/requests/highFiveList";
    }

    @GetMapping("/responseHighFive")
    @ResponseBody
    public String responseHighFive(@RequestParam("requestNo") int requestNo, @RequestParam("type") String type) {
        // 1. 조회, null 일경우 0 반환
        int requestCount = chatService.countHighFiveRequestByRequestNo(requestNo);


        // 2. 해당하는 data 있으면 동작
        if (requestCount != 0) {
            // type에 따라 수락인지 거절인지 분기
            HighFiveRequest highFiveRequest = chatService.selectHighFiveRequestByRequestNo(requestNo);
            Member member = ms.selectByMno(highFiveRequest.getSender());
            highFiveRequest.setNick(member.getNick());
            if (type.equals("accept")) {
                try {
                    chatService.removeAndInvite(highFiveRequest);
                } catch (Exception e) {
                    e.printStackTrace();
                    if (e.getMessage().equals("ADD_ERR") || e.getMessage().equals("DEL_ERR")) {
                        return "RESPONSE_ERR";
                    } else {
                        return "UNEXPECTED_ERR";
                    }
                }
                // 수락, 거절 했으니 헤더 알람에서 카운트 --
                return "ACCEPT";
            } else { // 거절일 경우 요청만 삭제
                try {
                    chatService.deleteHighFiveRequest(highFiveRequest);
                } catch (Exception e) {
                    e.printStackTrace();

                    return "DELETE_ERR";
                }
                // 수락, 거절 했으니 헤더 알람에서 카운트 --
                return "DECLINE";
            }
        } else {
            return "REQUEST_NOT_FOUND";
        }
    }
    @PatchMapping("/changeTitle")
    @ResponseBody
    public String changeTitle(@RequestBody ChatRoom chatRoom) {
        int mNo = (int) session.getAttribute("mNo");
        ChatRoom original = chatRoomService.selectChatRoomByCrNo(chatRoom.getCrNo());

        if(mNo != original.getAdmin()){
            return "WRONG_ADMIN";
        }

        try {
            chatRoomService.updateCrTitle(chatRoom);
            return "SUCCESS_TITLE";
        } catch (Exception e) {
            e.printStackTrace();
            return "FAIL_TITLE";
        }
    }
    @GetMapping("/kickUser")
    @ResponseBody
    public String kickUser(@RequestParam String target) {
        int mNo = (int) session.getAttribute("mNo");
        int targetNo = Integer.parseInt(target);
        ChatUser targetUser = chatUserService.selectByUserNo(targetNo);
        ChatRoom chatRoom = chatRoomService.selectChatRoomByCrNo(targetUser.getCrNo());
        if(chatRoom.getAdmin() != mNo){
            return "NOT_ADMIN";
        }

        try {
            Member member = ms.selectByMno(targetUser.getMNo());
            targetUser.setNick(member.getNick());
            chatService.kickUserOut(targetUser);
            return "SUCCESS_KICK";
        } catch (Exception e) {
            e.printStackTrace();
            return "FAIL_KICK";
        }
    }
    @GetMapping("/inherit")
    @ResponseBody
    public String inheritKing(@RequestParam String target) {
        int mNo = (int) session.getAttribute("mNo");
        int targetNo = Integer.parseInt(target);
        ChatUser targetUser = chatUserService.selectByUserNo(targetNo);
        ChatRoom chatRoom = chatRoomService.selectChatRoomByCrNo(targetUser.getCrNo());
        if(chatRoom.getAdmin() != mNo){
            return "NOT_ADMIN";
        }
        try {
            Member member = ms.selectByMno(targetUser.getMNo());
            targetUser.setNick(member.getNick());
            chatService.changeKing(targetUser);
            return "SUCCESS_KING";
        } catch (Exception e) {
            e.printStackTrace();
            return "FAIL_KING";
        }
    }
    @GetMapping("/checkKing")
    @ResponseBody
    public String checkKing(@RequestParam int target, @RequestParam int crNo) {
        int mNo = (int) session.getAttribute("mNo");
        if(target != mNo){
            return "WRONG_TARGET";
        }
        ChatRoom chatRoom = chatRoomService.selectChatRoomByCrNo(crNo);
        int king = chatRoom.getAdmin();
        if(target == king){
            return "isKing";
        }else{
            return "NotKing";
        }
    }


}
