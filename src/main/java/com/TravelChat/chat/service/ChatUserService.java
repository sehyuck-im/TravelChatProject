package com.TravelChat.chat.service;

import com.TravelChat.chat.model.ChatHistory;
import com.TravelChat.chat.model.ChatUser;

import java.util.List;

public interface ChatUserService {
    // chat user 생성
    void insertChatUser(int crNo, int mNo, int chNo) throws Exception;

    ChatUser selectByUserNo(int userNo);

    List<ChatUser> selectCrNo(String crNo);
    // mNo select
    int selectMNobyUserNo(int userNo);
    // 채팅방에 있는 사람들 정보 가져오기
    List<ChatUser> selectByCrNo(int crNo);
    // 마지막으로본 메세지 번호 업데이트
    void updateChNo(ChatUser updateChatUser);
    // 챗 유저 중 같은 챗팅방에 몇명있는지
    int getCountAtChatRoom(int crNo);
    // mNo으로 chatUser 가져오기
    List<ChatUser> selectChatUserListByMNo(int mNo);
    // chatUser select w/ mNo and crNo
    int selectUserNoByMNoAndCrNo(int mNo, int crNo);
    // chatUser의 닉네임 가져오기
    String getUserNick(int userNo);
    // mNo으로 chatUser 채팅방 업데이트 순서로 가져오기
    List<ChatUser> selectChatUserListByMNoSortTime(int mNo);
    // 해당하는 유저가 있는지 확인
    int countUserNoByMNoAndCrNo(int mNo, int crNo);
    // 채팅방 떠나기 tx 처리
    ChatHistory leaveChatRoomTransaction(ChatUser chatUser) throws Exception;
    // status 'o'로 변경
    void changeStatusOut(ChatUser chatUser) throws Exception;

    void kickedOut(ChatUser targetUser) throws Exception;

    ChatUser selectByUserNoWithOutStatus(int userNo);

    ChatUser selectOldestUser(int crNo, int userNo);

    int countAllUserByMNo(int mNo);

    void memberLeave(int mNo) throws Exception;

    List<ChatUser> selectByCrNoNUsers(int crNo);
}
