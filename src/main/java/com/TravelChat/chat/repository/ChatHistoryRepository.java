package com.TravelChat.chat.repository;

import com.TravelChat.chat.model.ChatHistory;
import com.TravelChat.chat.model.ChatUser;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface ChatHistoryRepository {
    void insert(ChatHistory chatHistory);

    List<ChatHistory> selectPastMsg(ChatHistory chatHistory);

    List<ChatHistory> selectLateMsg(ChatHistory chatHistory);

    ChatHistory selectLastMsg(ChatHistory chatHistory);

    void updateReadCount(ChatHistory chatHistory);

    int selectLastChNo(int crNo);

    int selectMyLastRead(int userNo);
    // 채팅방 생성시 들어가는 메세지
    void insertFirstMessage(ChatHistory ChatHistory);
    // 안읽은 채팅 개수
    int getUnreadCnt(ChatUser chatUser);
    // 메세지 10개 가져오기
    List<ChatHistory> selectLast10Msg(ChatHistory chatHistory);
    // 안읽은 메세지 가져오기
    List<ChatHistory> selectUnreadMsg(ChatHistory chatHistory);
    // 읽은 메세지 10개 가져오기
    List<ChatHistory> selectReadMsg(ChatHistory chatHistory);
    // 지난 메세지 가져오기
    List<ChatHistory> selectPast10Msg(ChatHistory chatHistory);
    // 해당 방의 제일 최신글번호 가져오기
    int selectMaxChNo(int crNo);
    // 메세지 insert
    void insertMessage(ChatHistory chatHistory);
    // 해당 채팅방의 마지막 메세지, 보낸 닉네임
    ChatHistory selectLastChatAtChatRoom(int crNo);
    // 마지막으로 메세지 보낸 유저 번호
    int selectLastMsgSendUserNo(int crNo);
    // 시스템 메세지 불러오기
    String selectSystemMsgByCrNo(int crNo);
    // 채팅방 나감 메세지 전송
    void sendOutMsg(ChatHistory chatHistory);
    // chNo으로 select
    ChatHistory selectMsgByChNo(int chNo);
    // 그룹챗 시스템 메세지
    void insertGroupMsg(ChatHistory chatHistory) throws Exception;

    int countChatHistoryByCrNo(int crNo);
}
