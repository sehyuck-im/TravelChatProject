package com.TravelChat.admin.repository;

import com.TravelChat.board.model.SearchCondition;
import com.TravelChat.chat.model.ChatHistory;
import com.TravelChat.chat.model.ChatRoom;
import com.TravelChat.chat.model.ChatRoomDeliver;
import com.TravelChat.chat.model.ChatUser;
import com.TravelChat.common.model.Comment;
import com.TravelChat.common.model.Feed;
import com.TravelChat.member.model.Member;
import com.TravelChat.member.model.Report;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Mapper
@Repository
public interface AdminRepository {

    int countChatRoomsCreatedToday(String strDate);

    int countMembersJoinedToday(String strDate);

    int countFeedsCreatedToday(String strDate);

    int countReportsCreatedToday(String strDate);

    List<Report> getUncheckedReport();

    ChatUser selectTargetChatUserByUserNo(int userNo);

    List<ChatHistory> selectAllHistoryByChatUser(ChatUser targetUser);

    List<ChatUser> selectAllUsersAtChatRoom(int crNo);

    void suspendMember(Member member) throws Exception;

    void checkReport(Report report);

    void suspendReportHistory(Report report) throws Exception;

    int countReportHistoryByMNo(int mNo);

    void insertReportHistory(int mNo);

    void updateReportHistory(Report report) throws Exception;

    void updateReportChk(Report report) throws Exception;

    List<Report> getUncheckedReportList(int offset);

    List<Member> selectMemberList(SearchCondition sc);

    int countTotalMember();

    void releaseMember(int mNo) throws Exception;

    void updateSuspendCount(Member member) throws Exception;

    int countTotalFeed();

    List<Feed> selectFeedList(SearchCondition sc);

    List<Comment> selectCommentListByFeedNo(int feedNo);

    void recoverFeed(Feed feed) throws Exception;

    int countTotalChatRoom();

    List<ChatRoomDeliver> selectChatRoomList(SearchCondition sc);
}
