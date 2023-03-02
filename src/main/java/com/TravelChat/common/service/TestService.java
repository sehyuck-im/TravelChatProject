package com.TravelChat.common.service;

import com.TravelChat.chat.model.ChatHistory;
import com.TravelChat.chat.model.ChatUser;
import com.TravelChat.member.model.Member;

import java.util.List;

public interface TestService {
    Member memberSetter();

    ChatUser chatUserSetter();

    ChatUser selectTempUser(ChatUser chatUser);

    void insertTempChatMessage(ChatUser chatUser);

    void insertFiveChatUser(int i);

    List<ChatUser> selectThree();

    List<ChatUser> selectFive();

    void insertFiveChatHistory(int i);

    ChatHistory selectLastMsg();

    void updateThreeChatUser(ChatUser chatUser);

    void updateReadCount(ChatHistory chatHistory);

    void insertSixUser(int crNo, int i);

    List<ChatUser> selectUser(int crNo);

    void simpleMsg(int crNo);

    ChatHistory selectSimpleMsg(int crNo);
}
