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
    public String ranNumAndAlphabet(int length) {
//        Step.1.숫자의 경우 아스키코드로 변환시 48~57이 0~9로 표현됩니다.
//        Step.2.따라서 로직의 randim.ints() 메소드의 첫번째 파라미터를 48로 지정해준다. 두번째 파라미터는 알파벳의 제일끝이 122이므로 알파벳만 출력할때와 같이 122+1로 셋팅해줍니다.
//        Step.3.알파벳과 숫자만 출력되어야 하니깐 filter() 메소드를 활용해서 아스키코드의 범위를 제한 해줍니다.
//        Step.4.문자열 길이를 limit()메소드로 제한해줍니다.
//        Step.5.collect() 메소드로 StringBuilder 객체를 생성해줍니다.
//        Step.6. 이제 StringBuilder 객체를 toString() 으로 문자열로 변환합니다.
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        Random random = new Random();
        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
        return generatedString;
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
