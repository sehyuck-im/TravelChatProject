package com.TravelChat.admin.controller;

import com.TravelChat.admin.service.AdminService;
import com.TravelChat.board.model.PageHandler;
import com.TravelChat.board.model.SearchCondition;
import com.TravelChat.board.service.BoardService;
import com.TravelChat.chat.model.ChatHistory;
import com.TravelChat.chat.model.ChatRoom;
import com.TravelChat.chat.model.ChatRoomDeliver;
import com.TravelChat.chat.model.ChatUser;
import com.TravelChat.chat.service.ChatHistoryService;
import com.TravelChat.chat.service.ChatRoomService;
import com.TravelChat.chat.service.ChatUserService;
import com.TravelChat.common.model.Comment;
import com.TravelChat.common.model.Feed;
import com.TravelChat.common.service.CommentService;
import com.TravelChat.common.service.FeedService;
import com.TravelChat.member.model.Member;
import com.TravelChat.member.model.Profile;
import com.TravelChat.member.model.Report;
import com.TravelChat.member.service.MemberService;
import com.TravelChat.member.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.time.LocalDate;
import java.util.*;

@Controller
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private HttpSession session;
    @Autowired
    private AdminService adminService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private ReportService reportService;
    @Autowired
    private FeedService feedService;
    @Autowired
    private ChatUserService chatUserService;
    @Autowired
    private ChatHistoryService chatHistoryService;
    @Autowired
    private ChatRoomService chatRoomService;
    @Autowired
    private BoardService boardService;
    @Autowired
    private CommentService commentService;

    @RequestMapping("/main")
    public String main(Model model, SearchCondition sc){
        // mNo != 1 이 아니면 못들어오게 만들것
        int mNo = (int) session.getAttribute("mNo");
        if(mNo != 1){
            return "redirect:/main";
        }

        // 1. 오늘 날짜 yyyy-mm-dd 가져오기
        // 2. 채팅방, 멤버, 피드, 신고
        LocalDate today = LocalDate.now();
        String strDate = today.toString();

        int chatRoomCount = adminService.countChatRoomsCreatedToday(strDate);
        int memberCount = adminService.countMembersJoinedToday(strDate);
        int feedCount = adminService.countFeedsCreatedToday(strDate);
        int reportCount = adminService.countReportsCreatedToday(strDate);

        int reportTotalCount = reportService.countAllUnchecked();
        PageHandler ph = new PageHandler(reportTotalCount, sc);
        
        List<Report> reportList;

        if(sc.getPage() == 1){ // 첫번째 리스트
            reportList = adminService.getUncheckedReport();

        }else{ // 그외 요청이 들어온거라면 offser 설정해서 가져오기
            int offset = sc.getOffset();
            reportList = adminService.getUncheckedReportList(offset);

        }

        for(Report report : reportList){
            Member reporter = memberService.selectByMno(report.getReporter());
            Member target = memberService.selectByMno(report.getTarget());
            report.setReporterNick(reporter.getNick());
            report.setTargetNick(target.getNick());
        }
        model.addAttribute("ph", ph);
        model.addAttribute("chatRoomCount", chatRoomCount);
        model.addAttribute("memberCount", memberCount);
        model.addAttribute("feedCount", feedCount);
        model.addAttribute("reportCount", reportCount);
        model.addAttribute("reportList", reportList);
        model.addAttribute("reportTotalCount", reportTotalCount);


        return "adminPage/index";
    }
    @RequestMapping("/detail")
    public String detail(@RequestParam("reportNo") int reportNo, Model model){
        Report report = reportService.selectByReportNo(reportNo);
        Member reporter = memberService.selectByMno(report.getReporter());
        Member target = memberService.selectByMno(report.getTarget());
        report.setReporterNick(reporter.getNick());
        report.setTargetNick(target.getNick());

        // 1. feed 일 경우 feed 보여주기
        if(report.getType() == 1){
            Feed feed = feedService.selectFeedByFeedNo(report.getEvidence());
            String[] photoArr = feed.getPhoto().split(",");
            if (photoArr.length > 1) { // 사진이 두장 이상이면
                for (String tempPhoto : photoArr) {
                    feed.getPhotoNames().add(tempPhoto);
                }
            }
            String targetPhoto = memberService.selectPhotoByMno(target.getMNo());
            target.setPhoto(targetPhoto);
            model.addAttribute("target", target);
            model.addAttribute("feed", feed);

        }else if(report.getType() == 2){
            // 2. profile 일 경우 profile 사진들, info 보여주기
            String profilePhoto = memberService.selectPhotoByMno(target.getMNo());
            if(!profilePhoto.equals("none")){
                String[] profilePhotos = adminService.selectAllProfilePhoto(target.getMNo());
                model.addAttribute("profilePhotos", profilePhotos);
            }
            Profile targetProfile = memberService.selectProfileByMno(target.getMNo());
            model.addAttribute("targetProfile", targetProfile);
            model.addAttribute("target", target);

        }else if(report.getType() == 3){
            // 3. chat 일 경우 채팅방 모든 내역 보여주기
            ChatUser targetUser = adminService.selectTargetChatUserByUserNo(report.getEvidence());
            ChatRoom chatRoom = chatRoomService.selectChatRoomByCrNo(targetUser.getCrNo());

            if(chatRoom.getGroupChat().equals("n")){
                chatRoom.setCrTitle(target.getNick()+"님과의 1:1 채팅");
            }

            List<ChatHistory> chatHistoryList = adminService.selectAllHistoryByChatUser(targetUser);
            List<ChatUser> chatUserAtChatRoom = adminService.selectAllUsersAtChatRoom(targetUser.getCrNo());
            for(ChatHistory chatHistory : chatHistoryList){
                for(ChatUser chatUser : chatUserAtChatRoom){
                    if(chatHistory.getUserNo() == chatUser.getUserNo()){
                        chatHistory.setNick(chatUser.getNick());
                    }
                }
                if(chatHistory.getUserNo() == 0){
                    chatHistory.setNick("System");
                }
            }

            model.addAttribute("chatRoom", chatRoom);
            model.addAttribute("chatHistoryList", chatHistoryList);
        }

        model.addAttribute("report", report);
        return "adminPage/detail";
    }


    @GetMapping("/suspendMember")
    @ResponseBody
    public String suspendMember(@RequestParam("reportNo")int reportNo){
        Report report = reportService.selectByReportNo(reportNo);
        Member member = memberService.selectByMno(report.getTarget());
        checkReportHistory(member.getMNo());
        try {
            adminService.suspendMemberAndSendEmail(member, report);
            return "SUCCESS";
        } catch (Exception e) {
            e.printStackTrace();
            return "FAIL";
        }
    }
    @GetMapping("/warnMember")
    @ResponseBody
    public String warnMember(@RequestParam("reportNo")int reportNo){
        Report report = reportService.selectByReportNo(reportNo);
        Member member = memberService.selectByMno(report.getTarget());
        checkReportHistory(member.getMNo());
        try {
            adminService.updateReportHistory(report);
            return "SUCCESS";
        } catch (Exception e) {
            e.printStackTrace();
            return "FAIL";
        }
    }
    @GetMapping("/checkReport")
    @ResponseBody
    public String checkReport(@RequestParam("reportNo")int reportNo){
        Report report = reportService.selectByReportNo(reportNo);
        Member member = memberService.selectByMno(report.getTarget());
        checkReportHistory(member.getMNo());
        try {
            adminService.updateReportChk(report);
            return "SUCCESS";
        } catch (Exception e) {
            e.printStackTrace();
            return "FAIL";
        }
    }
    @GetMapping("/memberPage")
    public String memberPage(Model model, SearchCondition sc){
        int totalCount = adminService.countTotalMember();
        PageHandler pageHandler = new PageHandler(totalCount, sc);
        List<Member> memberList = adminService.selectMemberList(sc);

        model.addAttribute("ph", pageHandler);
        model.addAttribute("memberList", memberList);
        model.addAttribute("totalCount", totalCount);

        return "/adminPage/memberPage";
    }

    @GetMapping("/memberDetail")
    public String memberDetail(@RequestParam("mNo") int mNo,Model model){
        Member target = memberService.selectByMno(mNo);
        // 작성한 피드 수, 참여 중인 채팅방, 작성한 게시판 글
        int feedCount = feedService.countAllFeedByWriter(mNo);
        int chatCount = chatUserService.countAllUserByMNo(mNo);
        int boardCount = boardService.countAllBoardByWriter(mNo);

        String profilePhoto = memberService.selectPhotoByMno(target.getMNo());
        if(!profilePhoto.equals("none")){
            String[] profilePhotos = adminService.selectAllProfilePhoto(target.getMNo());
            model.addAttribute("profilePhotos", profilePhotos);
        }
        Profile targetProfile = memberService.selectProfileByMno(target.getMNo());

        model.addAttribute("targetProfile", targetProfile);
        model.addAttribute("target", target);
        model.addAttribute("feedCount", feedCount);
        model.addAttribute("chatCount", chatCount);
        model.addAttribute("boardCount", boardCount);

        return "/adminPage/memberDetail";
    }

    @GetMapping("/suspendMemberByMNo")
    @ResponseBody
    public String suspendMemberByMNo(@RequestParam("mNo")int mNo){
        Report report = new Report();
        report.setReportNo(0);
        Member member = memberService.selectByMno(mNo);
        checkReportHistory(member.getMNo());
        try {
            adminService.suspendMemberAndSendEmail(member, report);
            return "SUCCESS";
        } catch (Exception e) {
            e.printStackTrace();
            return "FAIL";
        }
    }
    @GetMapping("/releaseMember")
    @ResponseBody
    public String releaseMember(@RequestParam("mNo")int mNo){

        try {
            adminService.releaseMember(mNo);
            return "SUCCESS";
        } catch (Exception e) {
            e.printStackTrace();
            return "FAIL";
        }
    }
    @GetMapping("/feedPage")
    public String feedPage(Model model, SearchCondition sc){
        int totalCount = adminService.countTotalFeed();
        PageHandler pageHandler = new PageHandler(totalCount, sc);
        List<Feed> feedList = adminService.selectFeedList(sc);

        model.addAttribute("ph", pageHandler);
        model.addAttribute("feedList", feedList);
        model.addAttribute("totalCount", totalCount);

        return "/adminPage/feedPage";
    }
    @GetMapping("/feedDetail")
    public String feedDetail(@RequestParam("feedNo") int feedNo, Model model) {
        Feed feed = feedService.selectFeedByFeedNo(feedNo);
        List<Comment> commentList = adminService.selectCommentListByFeedNo(feedNo);
        if(!commentList.isEmpty()){
            for(Comment comment : commentList){
                Member commenter = memberService.selectByMno(comment.getWriter());
                String profilePhoto = memberService.selectPhotoByMno(commenter.getMNo());
                commenter.setNick(commenter.getNick());
                commenter.setPhoto(profilePhoto);
            }
        }
        String[] photoArr = feed.getPhoto().split(",");
        if (photoArr.length > 1) { // 사진이 두장 이상이면
            for (String tempPhoto : photoArr) {
                feed.getPhotoNames().add(tempPhoto);
            }
        }
        feed.setCommentList(commentList);
        feed.setCommentCount(commentList.size());
        Member feedWriter = memberService.selectByMno(feed.getWriter());
        String profilePhoto = memberService.selectPhotoByMno(feedWriter.getMNo());

        model.addAttribute("feed", feed);
        model.addAttribute("profilePhoto", profilePhoto);
        model.addAttribute("nick", feedWriter.getNick());

        return "/adminPage/feedDetail";
    }
    @GetMapping("/changeFeedDel")
    @ResponseBody
    public String changFeedDel(@RequestParam("feedNo")int feedNo){
        Feed feed = feedService.selectFeedByFeedNo(feedNo);
        try {
            feedService.deleteFeed(feed);
            return "SUCCESS";
        } catch (Exception e) {
            e.printStackTrace();
            return "FAIL";
        }
    }
    @GetMapping("/recoverFeed")
    @ResponseBody
    public String recoverFeed(@RequestParam("feedNo")int feedNo){
        Feed feed = feedService.selectFeedByFeedNo(feedNo);
        try {
            adminService.recoverFeed(feed);
            return "SUCCESS";
        } catch (Exception e) {
            e.printStackTrace();
            return "FAIL";
        }
    }
    @GetMapping("/deleteComment")
    @ResponseBody
    public String deleteComment(@RequestParam("cno")int cno){
        Comment comment = commentService.selectCommentByCno(cno);
        try {
            commentService.deleteComment(comment);
            return "SUCCESS";
        } catch (Exception e) {
            e.printStackTrace();
            return "FAIL";
        }
    }
    @GetMapping("/chatPage")
    public String chatPage(Model model, SearchCondition sc){
        int totalCount = adminService.countTotalChatRoom();
        PageHandler pageHandler = new PageHandler(totalCount, sc);
        List<ChatRoomDeliver> chatRoomList = adminService.selectChatRoomList(sc);
        for(ChatRoomDeliver chatRoomDeliver : chatRoomList){
            int count = chatUserService.getCountAtChatRoom(chatRoomDeliver.getCrNo());
            chatRoomDeliver.setCount(count);
            if(chatRoomDeliver.getGroupChat().equals("n")){
                chatRoomDeliver.setCrTitle("1:1 채팅방");
            }
            int chatCount = chatHistoryService.countChatHistoryByCrNo(chatRoomDeliver.getCrNo());
            chatRoomDeliver.setChatCount(chatCount);
        }
        model.addAttribute("ph", pageHandler);
        model.addAttribute("chatRoomList", chatRoomList);
        model.addAttribute("totalCount", totalCount);

        return "/adminPage/chatPage";
    }
    @GetMapping("/chatDetail")
    public String chatDetail(@RequestParam("crNo") int crNo, Model model) {
        ChatRoom chatRoom = chatRoomService.selectChatRoomByCrNo(crNo);
        ChatRoomDeliver chatRoomDeliver = new ChatRoomDeliver();
        chatRoomDeliver.setAdmin(chatRoom.getAdmin());
        chatRoomDeliver.setCrNo(chatRoom.getCrNo());
        chatRoomDeliver.setCrTitle(chatRoom.getCrTitle());
        chatRoomDeliver.setGroupChat(chatRoom.getGroupChat());
        int count = chatUserService.getCountAtChatRoom(chatRoomDeliver.getCrNo());
        chatRoomDeliver.setCount(count);
        int chatCount = chatHistoryService.countChatHistoryByCrNo(chatRoomDeliver.getCrNo());
        chatRoomDeliver.setChatCount(chatCount);

        if(chatRoomDeliver.getGroupChat().equals("n")){
            chatRoomDeliver.setCrTitle("1:1 채팅방");
        }

        ChatUser temp = new ChatUser();
        temp.setCrNo(crNo);
        List<ChatHistory> chatHistoryList = adminService.selectAllHistoryByChatUser(temp);
        List<ChatUser> chatUserList = chatUserService.selectByCrNo(crNo);

        for(ChatHistory chatHistory : chatHistoryList){
            int userNo = chatHistory.getUserNo();
            if(userNo != 0){
                ChatUser chatUser = chatUserService.selectByUserNoWithOutStatus(userNo);
                Member member = memberService.selectByMno(chatUser.getMNo());
                chatHistory.setNick(member.getNick());
            }else{
                chatHistory.setNick("System");
            }
        }


        model.addAttribute("chatRoom", chatRoomDeliver);
        model.addAttribute("chatHistoryList", chatHistoryList);
        model.addAttribute("chatUserList", chatUserList);
        model.addAttribute("chatCount", chatCount);

        return "/adminPage/chatDetail";
    }

    private void checkReportHistory(int mNo){
        // report history check
        // 1. reportHisotry db에 있는지 확인
        // 2. 없으면 만들기
        int chk = adminService.countReportHistoryByMNo(mNo);
        if(chk == 0){
            adminService.insertReportHistory(mNo);
        }
    }

}
