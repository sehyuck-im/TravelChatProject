package com.TravelChat.service;

import com.TravelChat.common.service.EmailSendService;
import com.TravelChat.common.model.Email;
import com.TravelChat.common.model.EmailCode;
import com.TravelChat.member.service.MemberService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
//@Transactional
class EmailSendServiceImplTest {
    @Autowired
    private MemberService ms;
    @Autowired
    EmailSendService es;

    @Test
    void emailCodeSelectTest() throws Exception {
        EmailCode temp = new EmailCode();
        temp = es.selectEmailCode("imse2191@gmail.com");
        System.out.println("temp.getEmail() = " + temp.getEmail());

    }

    @Test
    void emailSendTest1(){
        Email email = new Email();
        email.setTo("imse2191@gmail.com");
        es.sendEmailCode(email);

    }

    @Test
    void emailCodeSendAndUpsertTest(){
        Email email = new Email();
        email.setTo("imse2191@gmail.com");
        // emailcode 전송
        email = es.sendEmailCode(email);

        // emailCode db에 upsert
        if(!email.getReason().equals("INVALID_EMAIL") && !email.getReason().equals("ERROR") &&
                !email.getReason().equals("")){
            EmailCode emailCode = new EmailCode(email.getTo(), email.getCode());
            int result = es.upsert(emailCode);
            if(result==1){
                System.out.println("insert 성공");
            }else if(result == 2){
                System.out.println("update 성공");
            }else {
                System.out.println("에러 발생");
            }
        }

        System.out.println(email.getReason());

    }
    @Test
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    void emailCodeDeleteTest(String email) throws Exception {
//        email = "test@test.com";
        int delResult = es.delete(email);
        System.out.println("delResult = " + delResult);
//        delResult = 0;
        if(delResult == 1){
            System.out.println("emailCode delete 성공");
        }else{
            System.out.println("emailCode delete 실패");
            throw new Exception("EmailCode delete 실패");
        }

    }
}