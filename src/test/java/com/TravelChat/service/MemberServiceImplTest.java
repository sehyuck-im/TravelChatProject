package com.TravelChat.service;

import com.TravelChat.common.service.EmailSendService;
import com.TravelChat.common.model.EmailCode;
import com.TravelChat.member.model.Member;
import com.TravelChat.member.model.Profile;
import com.TravelChat.member.service.MemberService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

@SpringBootTest
class MemberServiceImplTest {
    @Autowired
    MemberService ms;
    @Autowired
    EmailSendService ess;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private HttpSession session;

    @Test
    void deleteAndHistoryInsertWithTx(){
        Member member = ms.selectByMno(10);
        try {
            ms.leaveMember(member);
        } catch (Exception e) {
            e.printStackTrace();
            if(e.getMessage().equals("INSERT_ERR") || e.getMessage().equals("DELM_ERR") || e.getMessage().equals("DELP_ERR")){
                System.out.println(e.getMessage());
            }else{
                System.out.println(e.getMessage());
            }
        }
        System.out.println("완료");

    }

    @Test
    void nickAndProfileUpdateTestTx() {
        Member member = ms.selectByMno(10);
        member.setNick("test#4302");
        Profile profile = ms.selectProfileByMno(10);
        profile.setOpenGender("test22222");
        profile.setOpenProfile("test2222");
        profile.setIntro("testtesttest222222");


        try {
            ms.updateProfile(member, profile);
        } catch (Exception e) {
            e.getMessage();
        }
        member = ms.selectByMno(10);
        System.out.println("member = " + member);
        profile = ms.selectProfileByMno(10);
        System.out.println("profile = " + profile);
    }
    @Test
    void nickUpdateTestTx() throws Exception {
        Member member = ms.selectByMno(10);
        member.setNick("test#12341234");
        ms.updateNick(member);

    }

    @Test
    void separateCode(){
        String[] result = new String[2];
        System.out.println(result.length);
        String nick = "test#1234";
        result = nick.split("#");
        result[1] = "#" + result[1];

        System.out.println(Arrays.toString(result));
    }
    @Test
    void uploadPhoto(){
        Member member = ms.selectById("test1@gmail.com");
        Profile profile = ms.selectProfileByMno(member.getMNo());

        // 1. 디렉토리 있는지 확인
        String realPath = session.getServletContext().getRealPath("/resources/profile/"+member.getMNo());
        System.out.println("realPath = " + realPath);
        File createDir = new File(realPath);
        if(!createDir.exists()){
            // 존재하지 않으면 디렉토리 생성
            createDir.mkdir();
            System.out.println("폴더생성 완료");
        }else{
            System.out.println("이미 존재해서 안만듦");
        }

        // 2. 파일 이름 date 붙여서 저장
        Calendar calendar = Calendar.getInstance();
        Date date = new Date(calendar.getTimeInMillis());
        System.out.println("date = " + date);
        String fileName = date+member.getEmail();
        System.out.println("fileName = " + fileName);
        String test = "test.jpg";
        test = test.substring(test.lastIndexOf("."));
        System.out.println("test2 = " + test);

        // 3. 경로 지정


    }

    @Test
    void profileSelect(){
        Member member = ms.selectById("test0@gmail.com");


        Profile profile = ms.selectProfileByMno(member.getMNo());
        System.out.println("profile = " + profile);

            System.out.println("없는 회원, insert 해야함");
            // insert 후 profile model 전달

            System.out.println("있는 회원, model 전달");


    }
    @Test
    void insertMockProfile(){
        for(int i=1; i<=10; i++){
            String id = "test"+i+"@gmail.com";
            Member member = ms.selectById(id);
            Profile profile = new Profile();
            profile.setMNo(member.getMNo());
            profile.setIntro("test"+i);

            int result = ms.insertProfile(member.getMNo());
            System.out.println(i+"번째 result = " + result);
        }
    }
    @Test
    void splitCode(){
        Member member = ms.selectById("test@gmail.com");
        String[] nick = member.getNick().split("#");
        System.out.println(nick[0]);
        System.out.println(nick[1]);
        nick[1] = "#"+nick[1];
        System.out.println(nick[1]);
    }

    @Test
    void signInTest(){
        Member member = ms.selectById("test@gmail.com");
        member.setPassword("1234");
        Member tempMember = ms.selectById("test@gmail.com");
        if(tempMember == null){ // 일치하는 아이디가 없을 경우
            System.out.println("일치하는 아이디가 없습니다.");
        }else if(tempMember.getStatus().equals("s")){
            // 정지된 아이디
            System.out.println("정지된 아이디");
        }
        if(passwordEncoder.matches(member.getPassword(), tempMember.getPassword())){
            if(tempMember.getStatus().equals("n")){
                // 유효한 로그인
                System.out.println("유효한 로그인!");
            }

        }else{ // 아이디는 일치, 암호는 불일치
            System.out.println("아이디는 일치, 암호는 불일치");
        }
        System.out.println("test 끝");
    }
    @Test
    void ranNum() {
        Random ran = new Random();
        String code = "";
        int len = 6;
        for (int j = 1; j <= 100; j++) {
            for (int i = 0; i < len; i++) {
                String temp = Integer.toString(ran.nextInt(10));
                code += temp;

            }
            System.out.println(" final code = " + Integer.parseInt(code));

            code = "";

        }
    }
    @Test
    void insertMockMember() throws Exception {
        for(int i=1; i<=10; i++){
            Member member = new Member();
            member.setEmail("test"+i+"@gmail.com");
            member.setPassword(passwordEncoder.encode("1234"));
            String code = "#";
            Random ran = new Random();
            int len = 4;
            for (int j = 0; j < len; j++) {
                String temp = Integer.toString(ran.nextInt(10));
                code += temp;
            }
            member.setNick("test"+code);
            member.setGender("male");
            ms.insertMember(member);

        }
    }
    @Test
    void addCodeToNick() {
        String code = "#";
        Random ran = new Random();
        int len = 4;
        for (int i = 0; i < len; i++) {
            String temp = Integer.toString(ran.nextInt(10));
            code += temp;
        }
        System.out.println(" final code = " + code);
    }

    @Test
    void nickChk() {
        String nick = "imse";
        // code 생성
        String code = "#";
        Random ran = new Random();
        int len = 4;
        for (int i = 0; i < len; i++) {
            String temp = Integer.toString(ran.nextInt(10));
            code += temp;
        }
        System.out.println("first code = " + code);
        // 닉네임 중복체크
        int nickCnt = 1;
        // 중복시 반복
        while (nickCnt != 0) {
            code = "#";
            for (int i = 0; i < len; i++) {
                String temp = Integer.toString(ran.nextInt(10));
                code += temp;
            }
            System.out.println("before + code / nick = " + nick);
            System.out.println("before + nick / code = " + code);
            nickCnt = ms.selectNickCode(nick + code);
        }
        // 중복체크 후 nick+code 생성
        nick += code;
        System.out.println(" fianl nick = " + nick);

    }

//    @Test
//    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
//    void memberInsertTx() throws Exception {
//        // emailCode delete와 memberInsert가 하나의 tx로 구성
//        addMockDataForEmailCode();
//
//        Member member = new Member();
//        member.setEmail("test@test.com");
//        member.setNick("test#1234");
//        member.setPassword("test");
//        member.setGender("male");
//        int insertResult = ms.insertMember(member);
//        if (insertResult == 1) {
//            System.out.println("member insert 성공");
//        } else {
//            System.out.println("member insert 실패");
//            throw new Exception("Member insert 실패");
//        }
//
//        int delResult = ess.delete(member.getEmail());
//        delResult = 0;
//        if (delResult == 1) {
//            System.out.println("emailcode 삭제 성공");
//        } else {
//
//            throw new Exception("emailcode 삭제 실패");
//
//        }
//    }

    @Test
    void addMockDataForEmailCode() {
        EmailCode emailCode = new EmailCode("test@test.com", "testCode");
        int esResult = ess.upsert(emailCode);
        if (esResult == 1) {
            System.out.println("emailCode upsert 성공");
        } else {
            System.out.println("upsert 실패");
        }
    }

    @Test
    void addMockDataForMember() {

    }
}