package com.TravelChat.common.repository;

import com.TravelChat.common.model.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface CommentRepository {
    int insertComment(Comment comment) throws Exception;

    int insertPcno(Comment comment) throws Exception;

    List<Comment> selectCommentListByFeedNo(int feedNo);

    int updateComment(Comment comment) throws Exception;

    Comment selectCommentByCno(Integer cno);

    int deleteComment(Comment comment) throws Exception;

    int insertRepliedComment(Comment comment) throws Exception;

    int countRepliedCommentByCNo(Comment comment) throws Exception;

    int deleteComments(Comment comment) throws Exception;
}
