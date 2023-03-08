package com.TravelChat.board.controller;


import com.TravelChat.board.model.Board;
import com.TravelChat.board.model.PageHandler;
import com.TravelChat.board.model.SearchCondition;
import com.TravelChat.board.service.BoardService;
import com.TravelChat.member.model.Member;
import com.TravelChat.member.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.net.URI;
import java.util.List;

@RestController
public class BoardRestController {
    @Autowired
    private HttpSession session;
    @Autowired
    private MemberService memberService;
    @Autowired
    private BoardService boardService;

    @GetMapping("/board")
    public ModelAndView boardList(SearchCondition sc) {
        ModelAndView mv = new ModelAndView("/board/boardList");

        int totalCount = 0;
        // sc안에 option, keyword가 null 이면 검색이 아님

        if(sc.getKeyword().equals("") && sc.getOption().equals("")){
            try {
                totalCount = boardService.selectAllBoardCount();
                setBoardList(sc, mv, totalCount);
            } catch (Exception e) {
                e.printStackTrace();
                mv.addObject("msg", "LIST_ERR");
                mv.addObject("totalCount", 0);
            }
        }else{
            try {
                totalCount = boardService.selectSearchedCount(sc);
                setBoardList(sc, mv, totalCount);
            } catch (Exception e) {
                e.printStackTrace();
                mv.addObject("msg", "LIST_ERR");
                mv.addObject("totalCount", 0);
            }
        }

        return mv;
    }

    private void setBoardList(SearchCondition sc, ModelAndView mv, int totalCount) throws Exception {
        PageHandler pageHandler = new PageHandler(totalCount, sc);

        List<Board> boardList = boardService.getSearchResultPage(sc);
        for(Board board : boardList){
            Member member = memberService.selectByMno(board.getWriter());
            board.setNick(member.getNick());
            board.setPhoto(memberService.selectPhotoByMno(board.getWriter()));
        }

        mv.addObject("boardList", boardList);
        mv.addObject("ph", pageHandler);
        mv.addObject("totalCount", totalCount);
    }
    @GetMapping("/board/{bNo}")
    public ModelAndView boardDetail(@PathVariable int bNo) {
        int mNo = (int) session.getAttribute("mNo");
        ModelAndView mv = new ModelAndView("/board/detail");
        try {
            Board board = boardService.selectBoardByBNo(bNo);
            Member member = memberService.selectByMno(board.getWriter());
            String[] nickAndCode = memberService.separateNick(member.getNick());
            board.setNick(nickAndCode[0]);
            board.setCode(nickAndCode[1]);
            board.setPhoto(memberService.selectPhotoByMno(board.getWriter()));

            mv.addObject("mNo", mNo);
            mv.addObject("board", board);
        } catch (Exception e) {
            e.printStackTrace();
            mv = new ModelAndView("/board/boardList");
            mv.addObject("msg", "DETAIL_ERR");
            return mv;
        }

        return mv;
    }

    @GetMapping("/board/form")
    public ModelAndView boardInsertForm(Board board) {

        ModelAndView mv = new ModelAndView("/board/insertForm");
        mv.addObject("board", board);
        return mv;
    }
    @PostMapping("/board")
    public ResponseEntity<?> insertBoard(@RequestBody Board board) {
        int mNo = (int) session.getAttribute("mNo");
        board.setWriter(mNo);
        ModelAndView mv;
        String msg = "";
        try {
            // 게시글이 생성되면 채팅방도 같이 생성
            boardService.insertBoardAndCreateChatRoom(board);
            // 성공하면 list 페이지
            msg = "SUCCESS";
            return new ResponseEntity<>(msg, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("e.getMessage() = " + e.getMessage());
            // 실패하면 insertForm 페이지에서 다시 시도하게끔
            msg = "INSERT_ERR";

            return new ResponseEntity<>(msg, HttpStatus.NOT_MODIFIED);
        }
    }
    @PatchMapping("/board/{bNo}")
    public ResponseEntity<String> pullBoardUp(@PathVariable int bNo, String type, @RequestBody Board board){
        if(type.equals("pullingUp")){ // 게시글 끌어올리기 일때
            try {
                boardService.pullBoardUp(bNo);
            } catch (Exception e) {
                e.printStackTrace();
                return new ResponseEntity<String>("PULL_ERR", HttpStatus.BAD_REQUEST);
            }
        }else if(type.equals("form")){
            try {
                board.setBNo(bNo);
                boardService.updateBoard(board);
            } catch (Exception e) {
                e.printStackTrace();
                return new ResponseEntity<String>("MOD_ERR", HttpStatus.BAD_REQUEST);
            }
        }
        return new ResponseEntity<String>("SUCCESS", HttpStatus.OK);
    }

    @DeleteMapping("/board/{bNo}")
    public ResponseEntity<String> deleteBoard(@PathVariable int bNo){
        int mNo = (int) session.getAttribute("mNo");
        Board board;
        try {
            board = boardService.selectBoardByBNo(bNo);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<String>("SELECT_ERR", HttpStatus.NOT_ACCEPTABLE);
        }

        if(mNo == board.getWriter()){
            try {
                boardService.deleteBoard(board);
            } catch (Exception e) {
                e.printStackTrace();
                return new ResponseEntity<String>("DEL_ERR", HttpStatus.NOT_MODIFIED);
            }
        }else{
            return new ResponseEntity<String>("INCORRECT_ERR", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<String>("SUCCESS", HttpStatus.OK);
    }
}
