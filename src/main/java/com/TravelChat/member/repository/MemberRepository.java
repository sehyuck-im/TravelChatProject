package com.TravelChat.member.repository;


import com.TravelChat.common.model.EmailCode;
import com.TravelChat.member.model.Member;
import com.TravelChat.member.model.Profile;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface MemberRepository {
    //email chk methods
    int upsertEmailCode(EmailCode emailCode);
    int selectId(String email);
    EmailCode selectEmailCode(String email);
    // nick 중복체크
    int selectNickCode(String nick);
    // member insert
    void insertMember(Member member);
    // emailCode db 삭제
    int deleteEmailCode(String email);
    // select member by id
    Member selectById(String email);
    // count profile by mno
    int countProfileByMno(int mNo);
    // profile insert
    int insertProfile(int mNo);
    // select profile by mno
    Profile selectProfileByMno(int mNo);
    // select member by mno
    Member selectByMno(int mNo);
    // update photo
    void updatePhoto(Profile profile);
    // update nick
    int updateNick(Member member);
    // update profile without photo
    int updateProfileWithoutPhoto(Profile profile);
    // 비밀번호 변경
    void updatePassword(Member member);
    // 회원 기록 생성
    int insertMemberHistory(Member member);
    // 회원 삭제
    int deleteMember(Member member);
    // 프로필 삭제
    int deleteProfile(int mNo);
    // 프로필 사진 가져오기
    String selectPhotoByMno(int mNo);
    //test codes
    int insert(Member member);
    Member select(Member member);
    int update(Member member);
    int delete(Member member);
    Member selectBymNo(int mNo);

    int countMemberByEmail(String email);

    int countMemberByMNo(int memberNumber);
}
