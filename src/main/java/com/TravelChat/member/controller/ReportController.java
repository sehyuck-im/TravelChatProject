package com.TravelChat.member.controller;

import com.TravelChat.chat.model.ChatHistory;
import com.TravelChat.chat.model.ChatRoom;
import com.TravelChat.chat.model.ChatUser;
import com.TravelChat.chat.service.ChatHistoryService;
import com.TravelChat.chat.service.ChatRoomService;
import com.TravelChat.chat.service.ChatUserService;
import com.TravelChat.common.model.Feed;
import com.TravelChat.common.service.FeedService;
import com.TravelChat.member.model.Profile;
import com.TravelChat.member.model.Report;
import com.TravelChat.member.service.MemberService;
import com.TravelChat.member.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
public class ReportController {
    @Autowired
    private HttpSession session;
    @Autowired
    private FeedService feedService;
    @Autowired
    private ReportService reportService;
    @Autowired
    private ChatUserService chatUserService;

    @PostMapping("/report")
    public ResponseEntity<String> insertReport(@RequestBody Report report){
        int mNo = (int) session.getAttribute("mNo"); // 신고자
        report.setReporter(mNo);
        // 피드(1) 신고인지, 프로필(2) 신고인지, 채팅(3) 신고인지 나누기
        if(report.getType() == 1){
            // 피드 신고인 경우 feedNo
            Feed targetFeed = feedService.selectFeedByFeedNo(report.getEvidence());
            report.setTarget(targetFeed.getWriter());
        }else if(report.getType() == 2){
            // 프로필 신고 mNo
            report.setTarget(report.getEvidence());

        }else if(report.getType() == 3){
            // 채팅 신고 evidence은 userNo으로 보내줄것, target을 토대로 mNo 가져오기
            ChatUser chatUser = chatUserService.selectByUserNo(report.getEvidence());
            report.setTarget(chatUser.getMNo());
        }

        // 이미 신고한 경우가 있는지 확인
        int count = reportService.isReportCount(report);
        if(count > 0){
            return new ResponseEntity<String>("ALREADYDONE", HttpStatus.OK);
        }else{
            // report insert
            try {
                reportService.insertReport(report);
                return new ResponseEntity<String>("SUCCESS", HttpStatus.OK);
            } catch (Exception e) {
                e.printStackTrace();
                return new ResponseEntity<String>("FAIL_REPORT", HttpStatus.BAD_REQUEST);
            }
        }
    }

}
