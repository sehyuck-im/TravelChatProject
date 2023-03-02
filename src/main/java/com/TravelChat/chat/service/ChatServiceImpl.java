package com.TravelChat.chat.service;

import com.TravelChat.chat.model.*;
import com.TravelChat.chat.repository.ChatRepository;
import com.TravelChat.chat.repository.ChatRoomRepository;
import com.TravelChat.chat.repository.ChatUserRepository;
import com.TravelChat.member.model.ShakeRequest;
import com.TravelChat.member.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ChatServiceImpl implements ChatService {
    @Autowired
    private ChatRepository chatRepository;
    @Autowired
    private ChatUserRepository chatUserRepository;
    @Autowired
    private ChatRoomRepository chatRoomRepository;
    @Autowired
    private ChatRoomService chatRoomService;
    @Autowired
    private ChatUserService chatUserService;
    @Autowired
    private ChatHistoryService chatHistoryService;
    @Autowired
    private MemberService memberService;

    @Override
    public int createRequest(ChatRequest chatRequest) throws Exception {

        return chatRepository.createRequest(chatRequest);
    }

    @Override
    public int selectChatRequestCnt(ChatRequest chatRequest) {
        return chatRepository.selectChatRequestCnt(chatRequest);
    }

    @Override
    public List<ChatRequest> selectReceivedRequest(int mNo) {
        return chatRepository.selectReceivedRequest(mNo);
    }

    @Override
    public int selectReceivedRequestCnt(int mNo) {
        return chatRepository.selectReceivedRequestCnt(mNo);
    }

    @Override
    public ChatRequest selectChatRequest(int requestNo) {
        return chatRepository.selectChatRequest(requestNo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void createChatRoomAndDeleteRequest(ChatRequest chatRequest) throws Exception {
        // 1. 채팅방 생성
        int crNo = chatRoomService.createChatRoom();

        // 2. chatHistory 생성
        int readCount = chatUserService.getCountAtChatRoom(crNo);// chatUser 수로 초기 readCount 설정
        int chNo = chatHistoryService.insertFirstMessage(crNo, readCount);
        chatRoomService.updateTimeAtChatRoom(crNo); // 메세지 insert시 crNo updateTime update해주기
        // 3. chat user 생성
        chatUserService.insertChatUser(crNo, chatRequest.getSender(), chNo);
        chatUserService.insertChatUser(crNo, chatRequest.getReceiver(), chNo);
        // 4. 채팅 요청 삭제
        deleteRequest(chatRequest);

    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void deleteRequest(ChatRequest chatRequest) throws Exception {
        try {
            chatRepository.deleteChatRequest(chatRequest.getRequestNo());
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("CHATREQ_DEL_ERR");
        }
    }

    @Override
    public String setMsgList(List<ChatHistory> chatHistoryList, List<ChatUser> userList, int userNo) {
        String msg = "";

        for (ChatHistory chatHistory : chatHistoryList) {
            if (chatHistory.getUserNo() == userNo) { // 내가쓴 글일 때
                msg = setMyMsg(msg, chatHistory);
            }else if(chatHistory.getUserNo() == 0){ // userNo = 0, 시스템 메세지일 때
                msg = setSystemMsg(msg, chatHistory);
            }else { // 다른사람이 쓴 글 일때
                for (ChatUser chatUser : userList) {
                    if (chatUser.getUserNo() == chatHistory.getUserNo()) {
                        msg = setOtherMsg(chatHistory, chatUser, msg);
                    }
                }
            }
        }

        return msg;
    }

    @Override
    public String setSenderMsg(ChatHistory chatHistory) {
        String msg ="";
        msg = setMyMsg(msg, chatHistory);

        return msg;
    }

    @Override
    public String setReceivedMsg(ChatHistory chatHistory, ChatUser sender) {
        String msg = "";
        msg = setOtherMsg(chatHistory, sender, msg);

        return msg;
    }

    @Override
    public String setChatRoom(List<ChatUser> chatUserList, ChatRoomSet chatRoomSet) {
        String msg = "";

        msg += "<div class=\"row mt-3\" id=\""+chatUserList.get(0).getCrNo()+"\"><div class=\"col-4\"></div><div class=\"col-4\">";
        msg += "<div class=\"card border-secondary mb-3\"><div class=\"card-header\"><a href=\"/chat/chatRoom?crNo="+chatUserList.get(0).getCrNo()+"\" style=\"text-decoration: none;\">";
        msg += chatRoomSet.getRoomTitle()+"</a>";
        //안읽은 메세지 있는 경우 숫자 붙여주기
        if(chatRoomSet.getUnreadCnt() > 0){
            msg += "<span class=\"badge rounded-pill bg-secondary ms-1\" id=\"unreadCnt"+chatUserList.get(0).getCrNo()+"\">"+chatRoomSet.getUnreadCnt()+"</span>";
        }
        msg += "</div><div class=\"card-body\"><h4 class=\"card-title\">";
        for(ChatUser chatUser : chatUserList){ // 프로필 사진 붙여주기
            if(chatUser.getStatus().equals("n")){
                msg += "<img width=\"35\" height=\"35\" class=\"rounded-circle\"";
                if(chatUser.getPhoto().equals("none")){ // 사진이 없을 경우
                    msg += " src=\"../images/noPhoto.png\">";
                }else{
                    msg += " src=";
                    msg += "\"/image/profile/"+chatUser.getMNo()+"/"+chatUser.getPhoto()+"\">";
                }
            }
        }
        msg += "</h4><p class=\"card-text\"><div class=\"breadcrumb mt-2 mb-2\" id=\"lastMsg"+chatUserList.get(0).getCrNo()+"\">";
        if(chatRoomSet.getLastNick().equals("system")){ // 시스템 메세지가 마지막이라면
            msg += "<div class=\"breadcrumb-item active\">"+chatRoomSet.getLastMsg()+"</div>";
        }else{

            msg += "<div class=\"breadcrumb-item active\">"+chatRoomSet.getLastNick()+" : "+chatRoomSet.getLastMsg()+"</div>";
        }
        msg += "</div></p></div></div></div></div>";

        return msg;
    }

    @Override
    public String setSystemMsg(ChatHistory chatHistory) {
        String msg = "";
        msg += "<div class=\"row mt-3\"><div class=\"col-4\"></div>"+
                "<div class=\"alert alert-dismissible alert-secondary col-4\" align=\"center\">";
        msg += chatHistory.getMessage() + "</div></div>";
        return msg;
    }

    @Override
    public ChatRequest selectChatRequestBySenderAndReceiver(ChatRequest chatRequest) {
        return chatRepository.selectChatRequestBySenderAndReceiver(chatRequest);
    }

    @Override
    public int getReceivedChatRequest(int mNo) {
        return chatRepository.getReceivedChatRequest(mNo);
    }

    @Override
    public String setChatRequest(ChatRequest chatRequest) {
        String msg = "";
        msg += "<div class=\"card mt-2\" id=\"request"+chatRequest.getRequestNo()+"\">";
        msg += "<div class=\"card-body\"><h4 class=\"card-title\">";
        msg += "<a href=\"member/info?mNo="+chatRequest.getSender()+"\" style=\"text-decoration: none\">";
        msg += "<img width=\"35\" height=\"35\" class=\"rounded-circle\" src=\"";
        if(chatRequest.getPhoto().equals("none")){ // 사진이 없을 경우
            msg += "../images/noPhoto.png\" >";
        }else{
            msg += "/image/profile/"+chatRequest.getSender()+"/"+chatRequest.getPhoto()+"\">";
        }
        msg += "</a>";
        msg += "<span>"+chatRequest.getNick()+"</span><span class=\"text-muted\">"+chatRequest.getCode()+"</span>님으로부터 채팅 요청이 왔습니다.</h4>";
        msg += "<div align=\"center\" th:data=\""+chatRequest.getNick()+chatRequest.getCode()+"\">";
        msg += "<button type=\"button\" class=\"acceptBtn btn btn-outline-secondary\"" +
                "data=\""+chatRequest.getRequestNo()+","+"accept\">수락</button>"+
                "<button type=\"button\" class=\"declineBtn btn btn-outline-danger\"" +
                "data=\""+chatRequest.getRequestNo()+","+"decline\">거절</button>";
        msg += "</div></div></div>";

        return msg;
    }

    @Override
    public int getRequestCountByRequestNo(int requestNo) {
        return chatRepository.getRequestCountByRequestNo(requestNo);
    }

    @Override
    public String setShakeRequest(ShakeRequest shakeRequest) {
        String msg = "";
        msg += "<div class=\"card mt-2\" id=\""+shakeRequest.getShakeNo()+"\" ><div class=\"card-body\"><h4 class=\"card-title\">";
        msg += "<a href=\"member/info?mNo="+shakeRequest.getSender()+"\" style=\"text-decoration: none\">";
        msg += "<img width=\"35\" height=\"35\" class=\"rounded-circle\" src=\"";
        if(shakeRequest.getPhoto().equals("none")){
            msg += "../images/noPhoto.png\" >";
        }else{
            msg += "/image/profile/"+shakeRequest.getSender()+"/"+shakeRequest.getPhoto()+"\">";
        }
        msg += "</a>";
        msg += "<span>"+shakeRequest.getNick()+"</span><span class=\"text-muted\">"+shakeRequest.getCode()+"</span>님으로부터 친구 요청이 왔습니다.</h4>";
        msg += "<div align=\"center\" data=\""+shakeRequest.getNick()+shakeRequest.getCode()+"\">";
        msg += "<button type=\"button\" class=\"acceptBtn btn btn-outline-secondary\"" +
                "data=\""+shakeRequest.getShakeNo()+","+"accept\">수락</button>"+
                "<button type=\"button\" class=\"declineBtn btn btn-outline-danger\"" +
                "data=\""+shakeRequest.getShakeNo()+","+"decline\">거절</button>";
        msg += "</div></div></div>";

        return msg;
    }

    @Override
    public int countHighFiveRequestByMNoAndCrNo(int mNo, int crNo) {
        return chatRepository.countHighFiveRequestByMNoAndCrNo(mNo, crNo);
    }

    @Override
    public void insertHighFiveRequest(HighFiveRequest highFiveRequest) throws Exception {
        chatRepository.insertHighFiveRequest(highFiveRequest);
    }

    @Override
    public int countHighFiveRequestByMNo(int mNo) {
        return chatRepository.countHighFiveRequestByMNo(mNo);
    }

    @Override
    public List<HighFiveRequest> selectHighFiveRequestList(int mNo) {
        return chatRepository.selectHighFiveRequestList(mNo);
    }

    @Override
    public HighFiveRequest selectHighFiveRequestByRequestNo(int requestNo) {
        return chatRepository.selectHighFiveRequestByRequestNo(requestNo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void removeAndInvite(HighFiveRequest highFiveRequest) throws Exception {
        // 누구님이 입장하셨습니다 메세지 생성
        ChatHistory chatHistory = new ChatHistory();
        chatHistory.setCrNo(highFiveRequest.getCrNo());
        chatHistory.setMessage("We have another high-fiver!");
        chatHistory.setUserNo(0);
        int userCount = chatUserService.getCountAtChatRoom(highFiveRequest.getCrNo());
        chatHistory.setReadCount(userCount);
        chatHistoryService.insertMessage(chatHistory);
        //  sender를 채팅방에 초대하고(insert)
        ChatUser chatUser = new ChatUser();
        chatUser.setCrNo(highFiveRequest.getCrNo());
        chatUser.setChNo(chatHistory.getChNo());
        chatUser.setMNo(highFiveRequest.getSender());
        chatUserRepository.insertChatUser(chatUser);
        //  요청을 삭제
        chatRepository.deleteHighFiveRequest(highFiveRequest);
    }

    @Override
    public void deleteHighFiveRequest(HighFiveRequest highFiveRequest) throws Exception {
        chatRepository.deleteHighFiveRequest(highFiveRequest);
    }

    @Override
    public int countHighFiveRequestByRequestNo(int requestNo) {
        return chatRepository.countHighFiveRequestByRequestNo(requestNo);
    }

    @Override
    public HighFiveRequest selectHighFiveRequestBySenderAndCrNo(int sender, int crNo) {
        return chatRepository.selectHighFiveRequestBySenderAndCrNo(sender, crNo);
    }

    @Override
    public String setHighFiveRequest(HighFiveRequest highFiveRequest) {
        String msg = "";
        msg += "<div class=\"card mt-2\" id=\""+highFiveRequest.getRequestNo()+"\">";
        msg += "<div class=\"card-body\"><h4 class=\"card-title\">";
        msg += "<a href=\"member/info?mNo="+highFiveRequest.getSender()+"\" style=\"text-decoration: none\">";
        msg += "<img width=\"35\" height=\"35\" class=\"rounded-circle\" src=\"";
        if(highFiveRequest.getPhoto().equals("none")){ // 사진이 없을 경우
            msg += "../images/noPhoto.png\" >";
        }else{
            msg += "/image/profile/"+highFiveRequest.getSender()+"/"+highFiveRequest.getPhoto()+"\">";
        }
        msg += "</a>";
        msg += "<span>"+highFiveRequest.getNick()+"</span><span class=\"text-muted\">"+highFiveRequest.getCode()+"</span>님으로부터" +
                "<span class=\"text-info\">"+highFiveRequest.getTitle()+"</span>"+
                " 참가 요청이 왔습니다.</h4>";
        msg += "<div align=\"center\" data=\""+highFiveRequest.getNick()+highFiveRequest.getCode()+"\">";
        msg += "<button type=\"button\" class=\"acceptBtn btn btn-outline-secondary\"" +
                "data=\""+highFiveRequest.getRequestNo()+","+"accept\">수락</button>"+
                "<button type=\"button\" class=\"declineBtn btn btn-outline-danger\"" +
                "data=\""+highFiveRequest.getRequestNo()+","+"decline\">거절</button>";
        msg += "</div></div></div>";
        return msg;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void kickUserOut(ChatUser targetUser) throws Exception {
        // 1. 누구님을 내보냈습니다 메세지 전송
        ChatHistory chatHistory = new ChatHistory();
        chatHistory.setCrNo(targetUser.getCrNo());
        chatHistory.setMessage(targetUser.getNick()+"님을 내보냈습니다.");
        int readCount = chatUserService.getCountAtChatRoom(targetUser.getCrNo());
        chatHistory.setReadCount(readCount-1);
        chatHistoryService.insertMessage(chatHistory);
        // 2. targetUser status 변경
        chatUserService.kickedOut(targetUser);


    }

    @Override
    public void changeKing(ChatUser targetUser) throws Exception {
        // 1. target을 chatRoom admin으로 등록
        chatRoomRepository.changeKing(targetUser);

    }

    private String setMyMsg(String msg, ChatHistory chatHistory) {
        msg += "<div class=\"row mt-3\" id=\""+ chatHistory.getChNo()+"\"><div class=\"col-6\"></div><div class=\"col-6\">" +
                "<div class=\"d-flex justify-content-end\">";

        if(chatHistory.getReadCount() != 0){
            msg += "<div class=\"align-self-end\">"+
                    "<span class=\"text-muted me-1\" id=\"unreadCount"+chatHistory.getChNo()+"\">"+ chatHistory.getReadCount()+"</span></div>";
        }
        msg += "<div class=\"msg_container_send\">";
        msg += chatHistory.getMessage() + "</div></div></div></div>";
        return msg;
    }

    private String setSystemMsg(String msg, ChatHistory chatHistory) {
        msg += "<div class=\"row mt-3\" id=\""+chatHistory.getChNo()+"\"><div class=\"col-4\"></div>"+
                "<div class=\"alert alert-dismissible alert-secondary col-4\" align=\"center\">";
        msg += chatHistory.getMessage() + "</div></div>";
        return msg;
    }

    private String setOtherMsg(ChatHistory chatHistory, ChatUser sender, String msg) {
        msg += "<div class=\"col-6 mt-3\" id=\""+chatHistory.getChNo()+"\"><span><a href=\"/member/info?mNo=";
        msg += sender.getMNo();
        msg += "\" style=\"text-decoration: none\">";
        msg += "<img width=\"50\" height=\"50\" class=\"rounded-circle\"";
        if(sender.getPhoto().equals("none")){ // 사진이 없을 경우
            msg += " src=\"../images/noPhoto.png\">";
        }else{
            msg += " src=";

            msg += "\"/image/profile/"+sender.getMNo()+"/"+sender.getPhoto()+"\">";
        }

        msg += "</a><span class=\"ms-2\">"+sender.getNick()+"</span>" +
                "<span class=\"text-muted\">" +sender.getCode()+
                "</span>";

        msg += "<div class=\"d-flex mb-4\"><div class=\"msg_container\">";
        msg += chatHistory.getMessage()+"</div>";
        if(chatHistory.getReadCount() != 0){
            msg += "<div class=\"align-self-end\">"+
                    "<span class=\"text-muted me-1\" id=\"unreadCount"+chatHistory.getChNo()+"\">"+chatHistory.getReadCount()+"</span></div>";
        }
        msg += "</div></div></div>";
        return msg;
    }

}
