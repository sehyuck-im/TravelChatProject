package com.TravelChat.chat.repository;

import com.TravelChat.chat.model.ChatUser;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface ChatUserRepository {
    ChatUser selectByUserNo(int userNo);

    ChatUser selectTempUser(ChatUser chatUser);

    void insertFiveChatUser(ChatUser chatUser);

    List<ChatUser> selectThree(ChatUser chatUser);

    List<ChatUser> selectFive(ChatUser chatUser);

    void updateThreeChatUser(ChatUser chatUser);

    List<ChatUser> selectByMno(int mNo);

    List<ChatUser> selectUser(ChatUser chatUser);

    int selectLastMsgNo(int userNo);

    void updateChNo(ChatUser chatUser);
    // insert chat user
    void insertChatUser(ChatUser chatUser) throws Exception;
    // return mNo
    int selectMNobyUserNo(int userNo);
    // 채팅방에 있는 사람들 리스트
    List<ChatUser> selectByCrNo(int crNo);
    // 채팅방에 몇명있는지 select
    int getCountAtChatRoom(int crNo);
    // 해당 멤버의 chatUser 가져오기
    List<ChatUser> selectChatUserListByMNo(int mNo);
    // 채팅방에서 mNo으로 userNo select
    int selectUserNoByMNoAndCrNo(int mNo, int crNo);
    // 유저의 닉네임 가져오기
    String getUserNick(int userNo);
    // 해당 멤버의 chatUser 가져오기, 채팅방 업데이트 순서대로
    List<ChatUser> selectChatUserListByMNoSortTime(int mNo);
    // 해당 유저가 있는지 count
    int countUserNoByMNoAndCrNo(int mNo, int crNo);
    // 사용자가 나갔을 때 상태 변경
    void changeStatusOut(ChatUser chatUser);

    void kickedOut(ChatUser targetUser) throws Exception;

    ChatUser selectByUserNoWithOutStatus(int userNo);

    ChatUser selectOldestUser(int crNo, int userNo);

    int countAllUserByMNo(int mNo);

    void memberLeave(int mNo) throws Exception;

    List<ChatUser> selectByCrNoNUsers(int crNo);
}
