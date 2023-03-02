package com.TravelChat.common.service;

import com.TravelChat.common.model.Comment;
import com.TravelChat.common.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService{
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private FeedService feedService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertComment(Comment comment) throws Exception {
        feedService.updateCommentCount(comment.getFeedNo());
        commentRepository.insertComment(comment);

        return commentRepository.insertPcno(comment);
    }

    @Override
    public List<Comment> selectCommentListByFeedNo(int feedNo) {
        return commentRepository.selectCommentListByFeedNo(feedNo);
    }

    @Override
    public int updateComment(Comment comment) throws Exception {
        return commentRepository.updateComment(comment);
    }

    @Override
    public Comment selectCommentByCno(Integer cno) {
        return commentRepository.selectCommentByCno(cno);
    }

    @Override
    public int deleteComment(Comment comment) throws Exception {
        int count = 0;
        if(comment.getLevel() == 0){ // 댓글일 경우
            count = commentRepository.countRepliedCommentByCNo(comment);
            feedService.minusCommentCount(comment.getFeedNo(), count);

            return commentRepository.deleteComments(comment);

        }else{ // 대댓일경우
            count = 1;
            feedService.minusCommentCount(comment.getFeedNo(), count);
            return commentRepository.deleteComment(comment);
        }

    }

    @Override
    public int insertRepliedComment(Comment comment) throws Exception {
        feedService.updateCommentCount(comment.getFeedNo());

        return commentRepository.insertRepliedComment(comment);
    }
}
