package com.TravelChat.chat.service;

import com.TravelChat.chat.model.ChatRoom;

public interface ChatRoomService {
    // 채팅방 생성 후 crNo 받아오기
    int createChatRoom() throws Exception;
    // crNo으로 select
    ChatRoom selectChatRoomByCrNo(int crNo);
    // 채팅방 업데이트된 시간 업데이트
    void updateTimeAtChatRoom(int crNo);
    // admin
    int selectAdmin(int crNo);
    // 방제 변경
    void updateCrTitle(ChatRoom chatRoom) throws Exception;
}
