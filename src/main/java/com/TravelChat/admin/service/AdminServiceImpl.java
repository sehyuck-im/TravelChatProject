package com.TravelChat.admin.service;

import com.TravelChat.admin.repository.AdminRepository;
import com.TravelChat.board.model.SearchCondition;
import com.TravelChat.chat.model.ChatHistory;
import com.TravelChat.chat.model.ChatRoomDeliver;
import com.TravelChat.chat.model.ChatUser;
import com.TravelChat.common.model.Comment;
import com.TravelChat.common.model.Feed;
import com.TravelChat.common.service.EmailSendService;
import com.TravelChat.member.model.Member;
import com.TravelChat.member.model.Report;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.List;

@Service
public class AdminServiceImpl implements AdminService{
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private EmailSendService emailSendService;

    @Override
    public int countChatRoomsCreatedToday(String strDate) {

        return adminRepository.countChatRoomsCreatedToday(strDate);
    }

    @Override
    public int countMembersJoinedToday(String strDate) {
        return adminRepository.countMembersJoinedToday(strDate);
    }

    @Override
    public int countFeedsCreatedToday(String strDate) {
        return adminRepository.countFeedsCreatedToday(strDate);
    }

    @Override
    public int countReportsCreatedToday(String strDate) {
        return adminRepository.countReportsCreatedToday(strDate);
    }

    @Override
    public List<Report> getUncheckedReport() {
        return adminRepository.getUncheckedReport();
    }

    @Override
    public String[] selectAllProfilePhoto(int mNo) {
        String dirPath = "C:\\SoloProject\\image\\profile\\";
        File dir = new File(dirPath+mNo);
        String[] photos = dir.list();

        return photos;
    }

    @Override
    public ChatUser selectTargetChatUserByUserNo(int userNo) {
        return adminRepository.selectTargetChatUserByUserNo(userNo);
    }

    @Override
    public List<ChatHistory> selectAllHistoryByChatUser(ChatUser targetUser) {
        return adminRepository.selectAllHistoryByChatUser(targetUser);
    }

    @Override
    public List<ChatUser> selectAllUsersAtChatRoom(int crNo) {
        return adminRepository.selectAllUsersAtChatRoom(crNo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void suspendMemberAndSendEmail(Member member, Report report) throws Exception {
        // 1. 회원 상태 변경
        adminRepository.suspendMember(member);
        // 2. 정지되었다고 이메일 전송
        emailSendService.sendSuspendEmail(member);
        if(report.getReportNo() != 0){ // report가 있는 정지
            // 3. report chk 변경
            adminRepository.checkReport(report);
            // 4. ReportHistory update
            adminRepository.suspendReportHistory(report);
        }else{ // 관리자 검토 정지
            adminRepository.updateSuspendCount(member);
        }


    }

    @Override
    public int countReportHistoryByMNo(int mNo) {
        return adminRepository.countReportHistoryByMNo(mNo);
    }

    @Override
    public void insertReportHistory(int mNo) {
        adminRepository.insertReportHistory(mNo);
    }

    @Override
    public void updateReportHistory(Report report) throws Exception {
        adminRepository.updateReportHistory(report);
        adminRepository.updateReportChk(report);

    }

    @Override
    public void updateReportChk(Report report) throws Exception {
        adminRepository.updateReportChk(report);
    }

    @Override
    public List<Report> getUncheckedReportList(int offset) {
        return adminRepository.getUncheckedReportList(offset);
    }

    @Override
    public List<Member> selectMemberList(SearchCondition sc) {
        return adminRepository.selectMemberList(sc);
    }

    @Override
    public int countTotalMember() {
        return adminRepository.countTotalMember();
    }

    @Override
    public void releaseMember(int mNo) throws Exception {
        adminRepository.releaseMember(mNo);
    }

    @Override
    public int countTotalFeed() {
        return adminRepository.countTotalFeed();
    }

    @Override
    public List<Feed> selectFeedList(SearchCondition sc) {
        return adminRepository.selectFeedList(sc);
    }

    @Override
    public List<Comment> selectCommentListByFeedNo(int feedNo) {
        return adminRepository.selectCommentListByFeedNo(feedNo);
    }

    @Override
    public void recoverFeed(Feed feed) throws Exception {
        adminRepository.recoverFeed(feed);
    }

    @Override
    public int countTotalChatRoom() {
        return adminRepository.countTotalChatRoom();
    }

    @Override
    public List<ChatRoomDeliver> selectChatRoomList(SearchCondition sc) {
        return adminRepository.selectChatRoomList(sc);
    }

}
