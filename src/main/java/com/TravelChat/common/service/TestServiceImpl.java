package com.TravelChat.common.service;

import com.TravelChat.chat.model.ChatHistory;
import com.TravelChat.chat.model.ChatUser;
import com.TravelChat.member.model.Member;
import com.TravelChat.chat.repository.ChatHistoryRepository;
import com.TravelChat.chat.repository.ChatUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TestServiceImpl implements TestService {
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ChatUserRepository cur;
    @Autowired
    private ChatHistoryRepository chr;
    public Member memberSetter() {//테스트용 멤버 setter
        Member member= new Member();
//        member.setId("새로운 아이디 1");
        member.setEmail("이메일");
        member.setGender("성별");

        member.setNick("닉네임");

        String password=passwordEncoder.encode("비밀번호");
        member.setPassword(password);
        return member;
    }

    @Override
    public ChatUser chatUserSetter() {//테스트용 chatUser Setter
        ChatUser chatUser=new ChatUser();
        chatUser.setChNo(20);
        chatUser.setMNo(10);
        chatUser.setCrNo(124);
        return chatUser;
    }

    @Override
    public ChatUser selectTempUser(ChatUser chatUser) {//입력했던 임시 쳇 유저 select
        return cur.selectTempUser(chatUser);
    }

    @Override
    public void insertTempChatMessage(ChatUser chatUser) {//테스트용 더미 메세지 입력
        for(int i=0;i<100;i++)
        {
            ChatHistory chatHistory= new ChatHistory();
            chatHistory.setUserNo(chatUser.getUserNo());
            chatHistory.setMessage("테스트 메세지"+i);
            chatHistory.setCrNo(chatUser.getCrNo());

            chatHistory.setReadCount(1);
            chr.insert(chatHistory);
        }
    }

    @Override
    public void insertFiveChatUser(int i) {
        ChatUser chatUser=new ChatUser();
        chatUser.setChNo(1);
        chatUser.setMNo(i);
        chatUser.setCrNo(128);
        cur.insertFiveChatUser(chatUser);
    }

    @Override
    public List<ChatUser> selectThree() {
        ChatUser chatUser = new ChatUser();
        chatUser.setCrNo(128);
        return cur.selectThree(chatUser);
    }

    @Override
    public List<ChatUser> selectFive() {
        ChatUser chatUser = new ChatUser();
        chatUser.setCrNo(128);

        return cur.selectFive(chatUser);
    }

    @Override
    public void insertFiveChatHistory(int i) {
        ChatHistory chatHistory = new ChatHistory();
        chatHistory.setCrNo(128);
        chatHistory.setMessage("메세지 "+i);
        chatHistory.setUserNo(2);

        chatHistory.setReadCount(0);
        chr.insert(chatHistory);
    }

    @Override
    public ChatHistory selectLastMsg() {
        ChatHistory chatHistory = new ChatHistory();
        chatHistory.setCrNo(128);
        return chr.selectLastMsg(chatHistory);
    }

    @Override
    public void updateThreeChatUser(ChatUser chatUser) {
        cur.updateThreeChatUser(chatUser);
    }

    @Override
    public void updateReadCount(ChatHistory chatHistory) {
        chr.updateReadCount(chatHistory);
    }

    @Override
    public void insertSixUser(int crNo, int i) {
        ChatUser chatUser = new ChatUser();
        chatUser.setCrNo(crNo);
        chatUser.setMNo(i);
//        cur.insert(chatUser);
    }

    @Override
    public List<ChatUser> selectUser(int crNo) {
        ChatUser chatUser = new ChatUser();
        chatUser.setCrNo(crNo);

        return cur.selectUser(chatUser);
    }

    @Override
    public void simpleMsg(int crNo) {
        ChatHistory chatHistory = new ChatHistory();
        chatHistory.setCrNo(crNo);
        chatHistory.setMessage("Test Message");
        chatHistory.setUserNo(1);
        chatHistory.setReadCount(1);
        chr.insert(chatHistory);
    }

    @Override
    public ChatHistory selectSimpleMsg(int crNo) {
        ChatHistory chatHistory = new ChatHistory();
        chatHistory.setCrNo(crNo);
        return chr.selectLastMsg(chatHistory);
    }
}
