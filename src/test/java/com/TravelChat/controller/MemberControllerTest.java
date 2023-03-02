package com.TravelChat.controller;

import com.TravelChat.member.model.Member;
import com.TravelChat.member.service.MemberService;
import com.TravelChat.common.service.TestService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Random;

@SpringBootTest
class MemberControllerTest {
    @Autowired
    private MemberService ms;
    @Autowired
    private TestService ts;
    @Autowired
    private PasswordEncoder passwordEncoder;
    


    @Test
    void adminRegister() throws Exception {
        Member member = new Member();
        member.setEmail("travelplannerofficial@gmail.com");
        member.setPassword(passwordEncoder.encode("1234"));
        member.setNick("Travel Chat");
        member.setGender("admin");

        ms.insertMember(member);
        ms.insertProfile(1);
    }

    @Test
    void insertMockMember() throws Exception {
        for(int i=1; i<=20; i++){
            Member member = new Member();
            member.setEmail("test"+i+"@test.com");
            member.setPassword(passwordEncoder.encode("1234"));
            String nick = "Test";
            nick = ms.addNick(nick);
            member.setNick(nick);
            if(i%2==0){
                member.setGender("female");
            }else{
                member.setGender("male");
            }
            ms.insertMember(member);
            ms.insertProfile(i+1);
            System.out.println(i+"번째 insert done");
        }

    }

    @Test
    void stringSplitTest(){
        String str = "1";
        
        String[] strArr = str.split(",");

        System.out.println("Arrays.toString(strArr) = " + Arrays.toString(strArr));
        
        
    }
    @Test
    void Test_랜덤숫자알파벳() {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        Random random = new Random();
        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
        System.out.println(generatedString);
    }
//    @Test
//    void memberEmailChk() {
//
//    }
//
//    @Test//회원가입테스트
//    void memberInsert() {
//        //given
//        String redirect="memberInsertString";
//
//        Member member=ts.memberSetter();
//        int result=0;
//
//        //when
//            result=ms.insert(member);
//        //then
//        if(result==1)
//        {
//            System.out.println("회원가입 성공");
//        }
//    }
//    @Test
//    void memberSelect() {
//        //given
//        Member member=new Member();
//        member.setId("테스트2190480");
//        //when
//        int result=ms.insert(member);
//        Member tempMember=ms.select(member);
//        //then
//        if(member.getId().equals(tempMember.getId()))
//        {
//            System.out.println("셀렉 테스트 성공");
//        }
//
//    }
//    @Test//로그인 통합테스트
//    void 회원로그인테스트(){
//        //given
//    Member member=ts.memberSetter();
//    int result=ms.insert(member);
//        //when
//
//    if(result==1)
//    {
//        Member tempMember=ts.memberSetter();
//        member.setPassword("비밀번호");
//        result=ms.memberValChk(tempMember,member);
//    }
//    //then
//    else
//    {
//        System.out.println("통합로그인테스트 멤버 등록실패");
//    }
//
//        if(result==1)
//        {
//            System.out.println("통합 로그인 테스트 성공");
//        }
//        else{
//            System.out.println("결과타입:"+result);
//            System.out.println("통합 로그인 테스트 실패");
//        }
//    }
//
//    @Test
//    void 회원수정() {
//        //given
//        Member member = ts.memberSetter();
//        int result = ms.insert(member);
//
//        Member tempMember = ms.select(member);
//
//        //when
//        if(tempMember.getId().equals(member.getId())){
//            tempMember.setNick("변경된 닉네임");
//            tempMember.setGender("변경된 성별");
//
//            result = ms.update(tempMember);
//        }
//
//        if(result == 1) {
//            member = ms.select(tempMember);
//
//        }
//        //then
//        if(member.getNick().equals(tempMember.getNick()) &&
//                member.getGender().equals(tempMember.getGender())){
//            System.out.println("변경 성공");
//        }
//    }
//
//    @Test
//    void 회원탈퇴() {
//        //given
//        Member member = ts.memberSetter();
//        int result = ms.insert(member);
//
//        Member tempMember = ms.select(member);
//
//        //when
//        if(tempMember.getId().equals(member.getId())){
//
//            result = ms.delete(tempMember);
//        }
//        if(result == 1) {
//            member = ms.select(tempMember);
//        }
//
//        //then
//        if(member.getMDel().equals("y")){
//            System.out.println("삭제 성공");
//        }
//    }
//
    
}