package com.TravelChat.common.cotroller;

import com.TravelChat.common.model.Comment;
import com.TravelChat.common.service.CommentService;
import com.TravelChat.member.model.Member;
import com.TravelChat.member.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
public class CommentController {
    @Autowired
    private HttpSession session;
    @Autowired
    private CommentService commentService;
    @Autowired
    private MemberService memberService;

    @PostMapping("/comments")
    public ResponseEntity<String> insertComment(@RequestBody Comment comment, Integer feedNo){
        int mNo = (int) session.getAttribute("mNo");
        comment.setWriter(mNo);
        comment.setFeedNo(feedNo);
        if(comment.getPcno() == 0){ // 댓글일때
            try {
                if(commentService.insertComment(comment)!=1)
                    throw new Exception("COMMENT_INSERT_FAILED");

                return new ResponseEntity<String>("COMMENT_INSERT_OK", HttpStatus.OK);
            } catch (Exception e) {
                e.printStackTrace();
                return new ResponseEntity<String>("COMMENT_INSERT_ERR", HttpStatus.BAD_REQUEST);
            }
        }else{
            try { // 대댓글 일때
                if(commentService.insertRepliedComment(comment)!=1)
                    throw new Exception("COMMENT_INSERT_FAILED");

                return new ResponseEntity<String>("COMMENT_INSERT_OK", HttpStatus.OK);
            } catch (Exception e) {
                e.printStackTrace();
                return new ResponseEntity<String>("COMMENT_INSERT_ERR", HttpStatus.BAD_REQUEST);
            }
        }

    }

    @GetMapping("/comments")
    public ResponseEntity<List<Comment>> getComment(Integer feedNo){
        List<Comment> commentList = commentService.selectCommentListByFeedNo(feedNo);

        for(Comment comment : commentList){
            Member member = memberService.selectByMno(comment.getWriter());
            comment.setPhoto(memberService.selectPhotoByMno(member.getMNo()));
            String[] nickAndCode = memberService.separateNick(member.getNick());
            comment.setNick(nickAndCode[0]);
            comment.setCode(nickAndCode[1]);
        }

        return new ResponseEntity<List<Comment>>(commentList, HttpStatus.OK);
    }
    @PatchMapping("/comments/{cno}")
    public ResponseEntity<String> updateComment(@PathVariable Integer cno, @RequestBody Comment comment) {
        int mNo = (int) session.getAttribute("mNo");
        Comment temp = commentService.selectCommentByCno(cno);
        if(temp.getWriter() != mNo){
            return new ResponseEntity<String>("MOD_ERR", HttpStatus.BAD_REQUEST);
        }
        System.out.println("comment = " + comment);
        comment.setCno(cno);
        try {
            if(commentService.updateComment(comment) != 1)
                throw new Exception("Modify failed");

            return new ResponseEntity<String>("MOD_OK", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<String>("MOD_ERR", HttpStatus.BAD_REQUEST);
        }
    }
    @DeleteMapping("/comments/{cno}")
    public ResponseEntity<String> deleteComment(@PathVariable Integer cno) {
        int mNo = (int) session.getAttribute("mNo");
        Comment temp = commentService.selectCommentByCno(cno);

        if(temp.getWriter() != mNo){
            return new ResponseEntity<String>("DEL_ERR", HttpStatus.BAD_REQUEST);
        }else{
            try {
                if(commentService.deleteComment(temp) == 0)
                    throw new Exception("Delete failed");

                return new ResponseEntity<String>("DEL_OK", HttpStatus.OK);
            } catch (Exception e) {
                System.out.println("e.getMessage() = " + e.getMessage());
                e.printStackTrace();
                return new ResponseEntity<String>("DEL_ERR", HttpStatus.BAD_REQUEST);
            }
        }


    }
}
