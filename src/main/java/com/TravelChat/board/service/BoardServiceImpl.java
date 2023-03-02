package com.TravelChat.board.service;

import com.TravelChat.board.model.Board;
import com.TravelChat.board.model.SearchCondition;
import com.TravelChat.board.repository.BoardRepository;
import com.TravelChat.chat.model.ChatHistory;
import com.TravelChat.chat.model.ChatRoom;
import com.TravelChat.chat.repository.ChatHistoryRepository;
import com.TravelChat.chat.repository.ChatRoomRepository;
import com.TravelChat.chat.repository.ChatUserRepository;
import com.TravelChat.chat.service.ChatUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BoardServiceImpl implements BoardService{
    @Autowired
    private BoardRepository boardRepository;
    @Autowired
    private ChatRoomRepository chatRoomRepository;
    @Autowired
    private ChatHistoryRepository chatHistoryRepository;
    @Autowired
    private ChatUserService chatUserService;

    @Override
    public void insertBoard(Board board) throws Exception {

         boardRepository.insertBoard(board);
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void insertBoardAndCreateChatRoom(Board board) throws Exception {
        // 1. board insert
        insertBoard(board);
        ChatRoom chatRoom = new ChatRoom();
        // 2. 초기 title 설정 후 chatRoom create
        chatRoom.setCrTitle(board.getTitle());
        chatRoom.setAdmin(board.getWriter());
        chatRoomRepository.createGroupChatRoom(chatRoom);
        // 3. board에 crNo update
        boardRepository.updateChatRoomNo(chatRoom.getCrNo(), board.getBNo());
        // 4. 첫 메세지 insert
        ChatHistory chatHistory = new ChatHistory();
        chatHistory.setCrNo(chatRoom.getCrNo());
        chatHistoryRepository.insertGroupMsg(chatHistory);
        // 5. chatUser insert
        chatUserService.insertChatUser(chatRoom.getCrNo(), board.getWriter(), chatHistory.getChNo());

    }

    @Override
    public String selectBoardTitleByCrNo(int crNo) {
        return boardRepository.selectBoardTitleByCrNo(crNo);
    }

    @Override
    public int countAllBoardByWriter(int mNo) {
        return boardRepository.countAllBoardByWriter(mNo);
    }

    @Override
    public List<Board> selectAllBoardList() {
        return boardRepository.selectAllBoardList();
    }

    @Override
    public int selectAllBoardCount() throws Exception {
        return boardRepository.selectAllBoardCount();
    }

    @Override
    public List<Board> getSearchResultPage(SearchCondition sc) throws Exception {
        return boardRepository.getSearchResultPage(sc);
    }

    @Override
    public int selectSearchedCount(SearchCondition sc) throws Exception {
        return boardRepository.selectSearchedCount(sc);
    }

    @Override
    public Board selectBoardByBNo(int bNo) throws Exception{
        return boardRepository.selectBoardByBNo(bNo);
    }

    @Override
    public void pullBoardUp(int bNo) throws Exception {
        boardRepository.pullBoardUp(bNo);
    }

    @Override
    public void updateBoard(Board board) throws Exception {
        boardRepository.updateBoard(board);
    }

    @Override
    public void deleteBoard(Board board) throws Exception {
        boardRepository.deleteBoard(board);
    }




}
