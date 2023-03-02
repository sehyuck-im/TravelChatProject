package com.TravelChat.board.service;

import com.TravelChat.board.model.Board;
import com.TravelChat.board.model.SearchCondition;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface BoardService {

    void insertBoard(Board board) throws Exception;

    List<Board> selectAllBoardList();

    int selectAllBoardCount() throws Exception;

    List<Board> getSearchResultPage(SearchCondition sc) throws Exception;

    int selectSearchedCount(SearchCondition sc) throws Exception;

    Board selectBoardByBNo(int bNo) throws Exception;

    void pullBoardUp(int bNo) throws Exception;

    void updateBoard(Board board) throws Exception;

    void deleteBoard(Board board) throws Exception;

    @Transactional(rollbackFor = Exception.class)
    void insertBoardAndCreateChatRoom(Board board) throws Exception;

    String selectBoardTitleByCrNo(int crNo);

    int countAllBoardByWriter(int mNo);
}
