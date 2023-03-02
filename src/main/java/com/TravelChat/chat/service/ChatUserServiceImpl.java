package com.TravelChat.chat.service;

import com.TravelChat.chat.model.ChatHistory;
import com.TravelChat.chat.model.ChatUser;
import com.TravelChat.chat.repository.ChatUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ChatUserServiceImpl implements ChatUserService {
    @Autowired
    private ChatUserRepository chatUserRepository;
    @Autowired
    private ChatHistoryService chatHistoryService;
    @Autowired
    private ChatRoomService chatRoomService;


    @Override
    public ChatUser selectByUserNo(int userNo) {//primary key로 chatUser select
        return chatUserRepository.selectByUserNo(userNo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void insertChatUser(int crNo, int mNo, int chNo) throws Exception {
        ChatUser chatUser = new ChatUser();
        chatUser.setMNo(mNo);
        chatUser.setCrNo(crNo);
        chatUser.setChNo(chNo);
        try {
            chatUserRepository.insertChatUser(chatUser);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("CHATUSER_INSERT_ERR");
        }

    }

    @Override
    public List<ChatUser> selectCrNo(String crNo) {
        ChatUser chatUser= new ChatUser();
        int iCrNo=Integer.parseInt(crNo);
        chatUser.setUserNo(iCrNo);
        return chatUserRepository.selectUser(chatUser);
    }

    @Override
    public int selectMNobyUserNo(int userNo) {
        return chatUserRepository.selectMNobyUserNo(userNo);
    }

    @Override
    public List<ChatUser> selectByCrNo(int crNo) {
        return chatUserRepository.selectByCrNo(crNo);
    }

    @Override
    public void updateChNo(ChatUser updateChatUser) {
        chatUserRepository.updateChNo(updateChatUser);
    }

    @Override
    public int getCountAtChatRoom(int crNo) {
        return chatUserRepository.getCountAtChatRoom(crNo);
    }

    @Override
    public List<ChatUser> selectChatUserListByMNo(int mNo) {

        return chatUserRepository.selectChatUserListByMNo(mNo);
    }

    @Override
    public int selectUserNoByMNoAndCrNo(int mNo, int crNo) {
        return chatUserRepository.selectUserNoByMNoAndCrNo(mNo, crNo);
    }

    @Override
    public String getUserNick(int userNo) {
        return chatUserRepository.getUserNick(userNo);
    }

    @Override
    public List<ChatUser> selectChatUserListByMNoSortTime(int mNo) {
        return chatUserRepository.selectChatUserListByMNoSortTime(mNo);
    }

    @Override
    public int countUserNoByMNoAndCrNo(int mNo, int crNo) {
        return chatUserRepository.countUserNoByMNoAndCrNo(mNo, crNo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public ChatHistory leaveChatRoomTransaction(ChatUser chatUser) throws Exception {
        ChatHistory chatHistory = new ChatHistory();
        chatHistory.setNick(chatUser.getNick());
        chatHistory.setCrNo(chatUser.getCrNo());
        chatHistory.setReadCount(chatUser.getJoinPoint());
        chatHistory.setMessage(chatUser.getNick()+"님이 대화방을 나가셨습니다.");
        // 1. chatUser status 'y'로 변경
        try {
            changeStatusOut(chatUser);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("STATUS_CHANGE_ERR");
        }
        // 2. 시스템 메세지로 nick님이 채팅방을 나갔습니다. insert
        try {
            chatHistoryService.sendOutMsg(chatHistory);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("MSG_SEND_ERR");
        }
        // 3. chatRoom update time 변경
        chatRoomService.updateTimeAtChatRoom(chatUser.getCrNo());

        return chatHistory;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void changeStatusOut(ChatUser chatUser) throws Exception {
        chatUserRepository.changeStatusOut(chatUser);
    }

    @Override
    public void kickedOut(ChatUser targetUser) throws Exception {
        chatUserRepository.kickedOut(targetUser);
    }

    @Override
    public ChatUser selectByUserNoWithOutStatus(int userNo) {
        return chatUserRepository.selectByUserNoWithOutStatus(userNo);
    }

    @Override
    public ChatUser selectOldestUser(int crNo, int userNo) {
        return chatUserRepository.selectOldestUser(crNo, userNo);
    }

    @Override
    public int countAllUserByMNo(int mNo) {
        return chatUserRepository.countAllUserByMNo(mNo);
    }

    @Override
    public void memberLeave(int mNo) throws Exception {
        chatUserRepository.memberLeave(mNo);
    }

    @Override
    public List<ChatUser> selectByCrNoNUsers(int crNo) {
        return chatUserRepository.selectByCrNoNUsers(crNo);
    }

}
