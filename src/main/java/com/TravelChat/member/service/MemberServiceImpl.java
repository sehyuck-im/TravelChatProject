package com.TravelChat.member.service;

import com.TravelChat.chat.model.ChatUser;
import com.TravelChat.chat.service.ChatUserService;
import com.TravelChat.common.service.EmailSendService;
import com.TravelChat.member.model.Member;
import com.TravelChat.member.model.Profile;
import com.TravelChat.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

@Service
public class MemberServiceImpl implements MemberService {
    @Autowired
    private MemberRepository mr;
    @Autowired
    private EmailSendService ess;
    @Autowired
    private ChatUserService chatUserService;

    @Override
    public int idSelect(String email) {

        return mr.selectId(email);
    }

    @Override
    public String ranNum(int length) {
        Random ran = new Random();
        String code = "";
        for (int i = 0; i < length; i++) {
            String temp = Integer.toString(ran.nextInt(10));
            code += temp;
        }
        return code;
    }

    @Override
    public String addNick(String nick) {
        // code 생성
        String code = "#" + ranNum(4);
        // 닉네임 중복체크
        int nickCnt = selectNickCode(nick + code);
        // 중복시 반복
        while (nickCnt != 0) {
            code = "#"; // 중복시 코드 초기화
            code += ranNum(4);
            nickCnt = selectNickCode(nick + code);
        }
        // 중복체크 후 nick+code 생성
        nick += code;

        return nick;
    }


    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void insertAndDelete(Member member) throws Exception {
        try {
            ess.delete(member.getEmail());
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("DEL_ERR");
        }
        try {
            insertMember(member);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("INS_ERR");
        }

        insertProfile(member.getMNo());
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void insertMember(Member member) throws Exception {
        mr.insertMember(member);
    }

    @Override
    public Member selectById(String email) {
        return mr.selectById(email);
    }

    @Override
    public int countProfileByMno(int mNo) {
        return mr.countProfileByMno(mNo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public int insertProfile(int mNo) {
        return mr.insertProfile(mNo);
    }

    @Override
    public Profile selectProfileByMno(int mNo) {
        return mr.selectProfileByMno(mNo);
    }

    @Override
    public Member selectByMno(int mNo) {
        return mr.selectByMno(mNo);
    }

    @Override
    public String[] separateNick(String nick) {
        String[] result = new String[2];

        result = nick.split("#");
        result[1] = "#" + result[1];

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public int updateProfile(Member member, Profile profile) throws Exception {
        int nickResult = updateNick(member);
        int profileResult = updateProfileWithoutPhoto(profile);
        if (nickResult != 1) {
            throw new Exception("NICK_ERR");
        }
        if (profileResult != 1) {
            throw new Exception("PROFILE_ERR");
        }

        return (nickResult == 1 && profileResult == 1) ? 1 : 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public int updateProfileWithoutPhoto(Profile profile) throws Exception {

        return mr.updateProfileWithoutPhoto(profile);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public int updateNick(Member member) throws Exception {

        return mr.updateNick(member);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void updatePassword(Member member) throws Exception {

        mr.updatePassword(member);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void leaveMember(Member member) throws Exception {
        int insertResult = insertMemberHistory(member);
        int memberResult = deleteMember(member);
        int profileResult = deleteProfile(member.getMNo());
        chatUserService.memberLeave(member.getMNo()); // 모든 chatUser status 변경
        if (insertResult != 1) {
            throw new Exception("INSERT_ERR");
        }
        if (memberResult != 1) {
            throw new Exception("DELM_ERR");
        }
        if (profileResult != 1) {
            throw new Exception("DELP_ERR");
        }


    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public int insertMemberHistory(Member member) throws Exception {

        return mr.insertMemberHistory(member);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public int deleteMember(Member member) throws Exception {

        return mr.deleteMember(member);
    }

    @Override
    public int deleteProfile(int mNo) throws Exception {
        return mr.deleteProfile(mNo);
    }

    @Override
    public String selectPhotoByMno(int mNo) {
        return mr.selectPhotoByMno(mNo);
    }

    @Override
    public int countMemberByEmail(String email) {
        return mr.countMemberByEmail(email);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void updatePassAndSendEmail(Member member, String newPass) throws Exception {
        // 1. email 발송
        ess.sendNewPassEmail(member, newPass);
        // 2. 비밀번호 업데이트
        mr.updatePassword(member);
    }

    @Override
    public int countMemberByMNo(int memberNumber) {
        return mr.countMemberByMNo(memberNumber);
    }


    @Override
    public int selectNickCode(String nick) {
        return mr.selectNickCode(nick);
    }


}
