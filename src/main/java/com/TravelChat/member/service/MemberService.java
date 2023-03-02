package com.TravelChat.member.service;

import com.TravelChat.member.model.Member;
import com.TravelChat.member.model.Profile;

public interface MemberService {
    // email 기반 select
    int idSelect(String email);
    // 난수 생성
    String ranNum(int length);
    // code 생성 및 중복체크
    String addNick(String nick);
    // 닉네임 중복 체크
    int selectNickCode(String nick);
    // 멤버 insert 와 emailCode db 삭제
    void insertAndDelete(Member member) throws Exception;
    // member insert
    void insertMember(Member member) throws Exception;
    // 아이디로 member select
    Member selectById(String email);
    // mNo으로 해당 프로필 개수 체크
    int countProfileByMno(int mNo);
    // insert profile
    int insertProfile(int mNo);
    // mNo으로 프로필 셀렉트
    Profile selectProfileByMno(int mNo);

    Member selectByMno(int mNo);

    String[] separateNick(String nick);

    int updateProfile(Member member, Profile profile) throws Exception;

    int updateNick(Member member) throws Exception;

    int updateProfileWithoutPhoto(Profile profile) throws Exception;

    void updatePassword(Member member) throws Exception;

    void leaveMember(Member member) throws Exception;

    int insertMemberHistory(Member member) throws Exception;

    int deleteMember(Member member) throws Exception;

    int deleteProfile(int mNo) throws Exception;

    String selectPhotoByMno(int mNo);

    int countMemberByEmail(String email);

    String ranNumAndAlphabet(int length);

    void updatePassAndSendEmail(Member member, String newPass) throws Exception;

    int countMemberByMNo(int memberNumber);

//    int signIn(Member member, Member tempMember);

//    //Test codes
//    Member select(Member tempMember);
//
//    int delete(Member member);
//
//    int update(Member member);
//
////    int memberValChk(Member member, Member tempMember);
//
//    int insert(Member tempMember);
//
//    Member convertPassword(Member member);
}
