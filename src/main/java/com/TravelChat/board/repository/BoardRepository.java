package com.TravelChat.board.repository;


import com.TravelChat.board.model.Board;
import com.TravelChat.board.model.SearchCondition;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
@Mapper
@Repository
public interface BoardRepository {

    void insertBoard(Board board) throws Exception;

    List<Board> selectAllBoardList();

    int selectAllBoardCount() throws Exception;

    List<Board> getSearchResultPage(SearchCondition sc) throws Exception;


    int selectSearchedCount(SearchCondition sc) throws Exception;

    Board selectBoardByBNo(int bNo) throws Exception;

    void pullBoardUp(int bNo) throws Exception;

    void updateBoard(Board board) throws Exception;

    void deleteBoard(Board board) throws Exception;

    void updateChatRoomNo(int crNo, int bNo) throws Exception;

    String selectBoardTitleByCrNo(int crNo);

    int countAllBoardByWriter(int mNo);
}
