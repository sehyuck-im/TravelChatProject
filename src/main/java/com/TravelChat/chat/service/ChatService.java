package com.TravelChat.chat.service;

import com.TravelChat.chat.model.*;
import com.TravelChat.member.model.ShakeRequest;

import java.util.List;

public interface ChatService {
    // chat request insert
    int createRequest(ChatRequest chatRequest) throws Exception;
    // chat request cnt
    int selectChatRequestCnt(ChatRequest chatRequest);
    // chat request list select
    List<ChatRequest> selectReceivedRequest(int mNo);
    // 내가 받은 채팅 요청 cnt
    int selectReceivedRequestCnt(int mNo);
    // requestNo으로 select
    ChatRequest selectChatRequest(int requestNo);
    // 채팅방 만들고 요청 삭제하기
    void createChatRoomAndDeleteRequest(ChatRequest chatRequest) throws Exception;
    // 채팅 요청 삭제
    void deleteRequest(ChatRequest chatRequest) throws Exception;
    // 메세지 보내주기
    String setMsgList(List<ChatHistory> chatHistoryList, List<ChatUser> userList, int userNo);
    // 보낸 사람 메세지 틀 붙여주기
    String setSenderMsg(ChatHistory chatHistory);
    // 받은 메세지 틀 붙여주기
    String setReceivedMsg(ChatHistory chatHistory, ChatUser sender);
    // chatRoom list 틀 붙여주기
    String setChatRoom(List<ChatUser> chatUserList, ChatRoomSet chatRoomSet);
    // system msg set
    String setSystemMsg(ChatHistory chatHistory);
    // chat request select by sender and receiver
    ChatRequest selectChatRequestBySenderAndReceiver(ChatRequest chatRequest);
    // 받은 요청 합 가져오기
    int getReceivedChatRequest(int mNo);
    // chat request 틀 붙여주기
    String setChatRequest(ChatRequest chatRequest);
    // 해당하는 요청 있는지 확인
    int getRequestCountByRequestNo(int requestNo);
    // shake request 붙여주기
    String setShakeRequest(ShakeRequest shakeRequest);

    int countHighFiveRequestByMNoAndCrNo(int mNo, int crNo);

    void insertHighFiveRequest(HighFiveRequest highFiveRequest) throws Exception;

    int countHighFiveRequestByMNo(int mNo);

    List<HighFiveRequest> selectHighFiveRequestList(int mNo);

    HighFiveRequest selectHighFiveRequestByRequestNo(int requestNo);

    void removeAndInvite(HighFiveRequest highFiveRequest) throws Exception;

    void deleteHighFiveRequest(HighFiveRequest highFiveRequest) throws Exception;

    int countHighFiveRequestByRequestNo(int requestNo);

    HighFiveRequest selectHighFiveRequestBySenderAndCrNo(int sender, int crNo);

    String setHighFiveRequest(HighFiveRequest highFiveRequest);

    void kickUserOut(ChatUser targetUser) throws Exception;

    void changeKing(ChatUser targetUser) throws Exception;
}
