package com.TravelChat.chat.service;

import com.TravelChat.chat.model.ChatHistory;
import com.TravelChat.chat.model.ChatUser;
import com.TravelChat.chat.repository.ChatHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ChatHistoryServiceImpl implements ChatHistoryService {
    @Autowired
    private ChatHistoryRepository chatHistoryRepository;

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public int insertFirstMessage(int crNo, int readCount) throws Exception {
        ChatHistory chatHistory = new ChatHistory();
        chatHistory.setCrNo(crNo);
        chatHistory.setReadCount(readCount);
        try {
            chatHistoryRepository.insertFirstMessage(chatHistory);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("CHATHISTORY_INSERT_ERR");
        }
        return chatHistory.getChNo();
    }

    @Override
    public int getUnreadCnt(ChatUser chatUser) {

        return chatHistoryRepository.getUnreadCnt(chatUser);
    }

    @Override
    public List<ChatHistory> selectLast10Msg(ChatHistory chatHistory) {

        return chatHistoryRepository.selectLast10Msg(chatHistory);
    }

    @Override
    public List<ChatHistory> selectUnreadMsg(ChatHistory chatHistory) {
        return chatHistoryRepository.selectUnreadMsg(chatHistory);
    }

    @Override
    public List<ChatHistory> selectReadMsg(ChatHistory chatHistory) {
        return chatHistoryRepository.selectReadMsg(chatHistory);
    }

    @Override
    public List<ChatHistory> selectPast10Msg(ChatHistory chatHistory) {
        return chatHistoryRepository.selectPast10Msg(chatHistory);
    }

    @Override
    public int selectMaxChNo(int crNo) {
        return chatHistoryRepository.selectMaxChNo(crNo);
    }

    @Override
    public void updateReadCnt(ChatHistory updateChatHistory) {
        chatHistoryRepository.updateReadCount(updateChatHistory);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public ChatHistory insertMessage(ChatHistory chatHistory) throws Exception {
        chatHistoryRepository.insertMessage(chatHistory);

        return chatHistory;
    }

    @Override
    public ChatHistory selectLastChatAtChatRoom(int crNo) {
        return chatHistoryRepository.selectLastChatAtChatRoom(crNo);
    }

    @Override
    public int selectLastMsgSendUserNo(int crNo) {
        return chatHistoryRepository.selectLastMsgSendUserNo(crNo);
    }

    @Override
    public String selectSystemMsgByCrNo(int crNo) {

        return chatHistoryRepository.selectSystemMsgByCrNo(crNo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void sendOutMsg(ChatHistory chatHistory) throws Exception {
       chatHistoryRepository.sendOutMsg(chatHistory);
    }

    @Override
    public ChatHistory selectMsgByChNo(int chNo) {
        return chatHistoryRepository.selectMsgByChNo(chNo);
    }

    @Override
    public int getAllUnreadCount(List<ChatUser> myChatUserList) {
        int total = 0;
        for(ChatUser myChatUser : myChatUserList){
            int unreadCntAtRoom = getUnreadCnt(myChatUser);
            total += unreadCntAtRoom;
        }
        return total;
    }

    @Override
    public int countChatHistoryByCrNo(int crNo) {
        return chatHistoryRepository.countChatHistoryByCrNo(crNo);
    }
}
