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
                    //?????? ?????? ????????? 1:1 ???????????? ??????
                    isChatRoom = true;
                    crNo = receiverUser.getCrNo();

                }
            }
        }
        if(!isChatRoom){ // ???????????? ?????? ??????
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
        }else{ // ????????? ?????????
            result = "crNo="+crNo;
            return result;
        }
    }

    @GetMapping("/list")
    public String chatList(Model model) {
        int mNo = (int) session.getAttribute("mNo");
        Member member = ms.selectByMno(mNo);
        // ?????? ?????? ?????????
        List<ChatRequest> tempRequestList = chatService.selectReceivedRequest(member.getMNo());
        List<ChatRequest> requestList = new ArrayList<>();

        // ???????????? ???????????? ???????????? ????????? ?????? set ??????
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
        // 1. chatRequest ??????, null ????????? 0 ??????
        int requestCount = chatService.getRequestCountByRequestNo(requestNo);

        // 2. ???????????? data ????????? ??????
        if (requestCount != 0) {
            // type??? ?????? ???????????? ???????????? ??????
            ChatRequest chatRequest = chatService.selectChatRequest(requestNo);
            if (type.equals("accept")) { // ????????? ?????? ????????? ?????? ??? ?????? ??????
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
                            //?????? ?????? ????????? 1:1 ???????????? ??????
                            crNo = chatRoom.getCrNo();
                        }
                    }
                }

                return "ACCEPTED"+crNo;
            } else { // ????????? ?????? ????????? ??????

                try {
                    chatService.deleteRequest(chatRequest);
                } catch (Exception e) {
                    e.printStackTrace();
                    return "DELETE_ERR";
                }
                // ????????? ????????? ?????? ?????? ????????????
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

        if(cnt == 0){ // chatUser??? ???????????? crNo??? ?????????
            model.addAttribute("result", -1);

            return "chat/chatRoomBack";
        }

        ChatRoom chatRoom = chatRoomService.selectChatRoomByCrNo(ICrNo);
        List<ChatUser> chatUserList = chatUserService.selectByCrNoNUsers(ICrNo);
        int userNo = chatUserService.selectUserNoByMNoAndCrNo(mNo, ICrNo);
        ChatUser chatUser = chatUserService.selectByUserNo(userNo); // ?????????

        if(chatRoom.getGroupChat().equals("y")){ // ?????? ??????????????? ????????? ?????? set
            if(chatRoom.getCrTitle().equals("")){
                String title = boardService.selectBoardTitleByCrNo(ICrNo);
                chatRoom.setCrTitle(title);
            }
            for(ChatUser user : chatUserList){ // ????????? ?????? ??????
                String[] nickAndCode = ms.separateNick(user.getNick());
                user.setNick(nickAndCode[0]);
                user.setCode(nickAndCode[1]);
            }
            model.addAttribute("chatUserList", chatUserList);
            model.addAttribute("roomTitle", chatRoom.getCrTitle());

        }else{
            for(ChatUser temp : chatUserList){
                if(temp.getMNo() != mNo){ // ????????? ???????????? room title set ?????????
                    model.addAttribute("roomTitle", temp.getNick()+"????????? ??????");
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
        // 1. ????????? ????????? ?????? ???????????? ????????? ??????, ????????? ???????????? ??????
        int userCount = chatUserService.countUserNoByMNoAndCrNo(mNo, crNo);
        int requestCount = chatService.countHighFiveRequestByMNoAndCrNo(mNo, crNo);
        if(userCount>0 || requestCount > 0){
            return "CHECK";
        }
        // 2. ?????? ?????????
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
        // 1. ??????, null ????????? 0 ??????
        int requestCount = chatService.countHighFiveRequestByRequestNo(requestNo);


        // 2. ???????????? data ????????? ??????
        if (requestCount != 0) {
            // type??? ?????? ???????????? ???????????? ??????
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
                // ??????, ?????? ????????? ?????? ???????????? ????????? --
                return "ACCEPT";
            } else { // ????????? ?????? ????????? ??????
                try {
                    chatService.deleteHighFiveRequest(highFiveRequest);
                } catch (Exception e) {
                    e.printStackTrace();

                    return "DELETE_ERR";
                }
                // ??????, ?????? ????????? ?????? ???????????? ????????? --
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
