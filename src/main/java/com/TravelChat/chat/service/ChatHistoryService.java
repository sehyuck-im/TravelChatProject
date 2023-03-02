package com.TravelChat.chat.service;

import com.TravelChat.chat.model.ChatHistory;
import com.TravelChat.chat.model.ChatUser;

import java.util.List;

public interface ChatHistoryService {
    // chatHistory insert
    int insertFirstMessage(int crNo, int readCount) throws Exception;
    // 특정방의 안읽은 메세지 수 
    int getUnreadCnt(ChatUser chatUser);
    // 메세지 10개 불러오기
    List<ChatHistory> selectLast10Msg(ChatHistory chatHistory);
    // 안읽은 메세지 가져오기
    List<ChatHistory> selectUnreadMsg(ChatHistory chatHistory);
    // 읽은 메세지 10개 가져오기
    List<ChatHistory> selectReadMsg(ChatHistory chatHistory);
    // 지난 10개 메세지 가져오기 (10개 미만이어도 가능)
    List<ChatHistory> selectPast10Msg(ChatHistory chatHistory);
    // 해당 채팅방의 제일 최신글 번호 불러오기
    int selectMaxChNo(int crNo);
    // 해당하는 chatHistory readCount update
    void updateReadCnt(ChatHistory updateChatHistory);
    // 메세지 전송
    ChatHistory insertMessage(ChatHistory chatHistory) throws Exception;
    // 해당 채팅방의 제일 마지막 메세지와 보낸사람 닉네임
    ChatHistory selectLastChatAtChatRoom(int crNo);
    // 마지막으로 보낸 유저 번호
    int selectLastMsgSendUserNo(int crNo);
    // 시스템 메세지 불러오기
    String selectSystemMsgByCrNo(int crNo);
    // chatUser가 나갔습니다 메세지 전송
    void sendOutMsg(ChatHistory chatHistory) throws Exception;
    // chNo으로 select
    ChatHistory selectMsgByChNo(int chNo);
    // 안읽은 메세지 총 합 보여주기
    int getAllUnreadCount(List<ChatUser> myChatUserList);

    int countChatHistoryByCrNo(int crNo);
}
