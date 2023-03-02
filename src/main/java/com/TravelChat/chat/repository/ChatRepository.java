package com.TravelChat.chat.repository;

import com.TravelChat.chat.model.ChatRequest;
import com.TravelChat.chat.model.HighFiveRequest;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface ChatRepository {

    int createRequest(ChatRequest chatRequest);

    int selectChatRequestCnt(ChatRequest chatRequest);

    List<ChatRequest> selectReceivedRequest(int mNo);

    int selectReceivedRequestCnt(int mNo);

    ChatRequest selectChatRequest(int requestNo);

    void deleteChatRequest(int requestNo);

    ChatRequest selectChatRequestBySenderAndReceiver(ChatRequest chatRequest);

    int getReceivedChatRequest(int mNo);

    int getRequestCountByRequestNo(int requestNo);

    int countHighFiveRequestByMNoAndCrNo(int mNo, int crNo);

    void insertHighFiveRequest(HighFiveRequest highFiveRequest) throws Exception;

    int countHighFiveRequestByMNo(int mNo);

    List<HighFiveRequest> selectHighFiveRequestList(int mNo);

    HighFiveRequest selectHighFiveRequestByRequestNo(int requestNo);

    void deleteHighFiveRequest(HighFiveRequest highFiveRequest) throws Exception;

    int countHighFiveRequestByRequestNo(int requestNo);

    HighFiveRequest selectHighFiveRequestBySenderAndCrNo(int sender, int crNo);
}
