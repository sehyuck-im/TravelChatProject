package com.TravelChat.common.service;

import com.TravelChat.common.model.Comment;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CommentService {
    @Transactional(rollbackFor = Exception.class)
    int insertComment(Comment comment) throws Exception;

    List<Comment> selectCommentListByFeedNo(int feedNo);

    int updateComment(Comment comment) throws Exception;

    Comment selectCommentByCno(Integer cno);

    int deleteComment(Comment comment) throws Exception;

    int insertRepliedComment(Comment comment) throws Exception;
}
