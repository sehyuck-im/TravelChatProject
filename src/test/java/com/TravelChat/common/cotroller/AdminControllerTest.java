package com.TravelChat.common.cotroller;

import com.TravelChat.admin.service.AdminService;
import com.TravelChat.chat.model.ChatRoom;
import com.TravelChat.member.model.Member;
import com.TravelChat.member.model.Report;
import com.TravelChat.member.service.MemberService;
import com.TravelChat.member.service.ReportService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.time.LocalDate;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AdminControllerTest {
    @Autowired
    private AdminService adminService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private ReportService reportService;

    @Test
    void insertMockReport() throws Exception {
        for(int i=3; i<=24; i++){
            int target = 2;
            if(i != 13){
                for(int j=1; j<=3; j++){
                    Report report = new Report();
                    report.setReporter(i);
                    report.setTarget(target);
                    report.setEvidence(j);
                    report.setReason(j);
                    report.setType(j);

                    reportService.insertReport(report);

                }
            }
        }

    }
    @Test
    void getProfilePhotosTest(){
        String dirPath = "C:\\SoloProject\\image\\profile\\2";
        File dir = new File(dirPath);
        String[] names = dir.list();
        for(String temp : names){
            System.out.println("temp = " + temp);
        }
    }

    @Test
    void profileDetailSetTest(){
        Member target = memberService.selectByMno(12);
        String profilePhoto = memberService.selectPhotoByMno(target.getMNo());
        if(profilePhoto.equals("none")){
            System.out.println("사진 없음!");
            System.out.println("profilePhoto = " + profilePhoto);
        }else{
            System.out.println("사진 있음!");
            System.out.println("profilePhoto = " + profilePhoto);
        }
    }

    @Test
    void getChatRoomTodayTest(){
        LocalDate today = LocalDate.now();
        System.out.println("today = " + today);
        String str = today.toString();
        int chatRoomCount = adminService.countChatRoomsCreatedToday(str);
        System.out.println("chatRoomCount = " + chatRoomCount);
    }

    @Test
    void convertDate(){
        LocalDate today = LocalDate.now();
        System.out.println("today = " + today);


        String str = today.toString();
        System.out.println("str = " + str);

    }

}