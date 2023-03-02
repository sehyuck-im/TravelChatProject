package com.TravelChat.service;

import com.TravelChat.member.repository.MemberRepository;
import com.TravelChat.member.service.MemberService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

class PhotoServiceImplTest {
    @Autowired
    private MemberService memberService;
    @Autowired
    private MemberRepository mr;

    @Test
    void getPath(){
        MultipartFile multipartFile;
        String projectPath = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\profile";
        System.out.println("projectPath = " + projectPath);

    }

}