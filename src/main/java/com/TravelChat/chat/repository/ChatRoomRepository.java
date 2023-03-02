package com.TravelChat.chat.repository;

import com.TravelChat.chat.model.ChatRoom;
import com.TravelChat.chat.model.ChatUser;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface ChatRoomRepository {
    void createChatRoom(ChatRoom chatRoom);

    ChatRoom selectChatRoom();

    ChatRoom selectChatRoomByCrNo(int crNo);

    void updateTimeAtChatRoom(int crNo);

    void createGroupChatRoom(ChatRoom chatRoom) throws Exception;

    int selectAdmin(int crNo);

    void updateCrTitle(ChatRoom chatRoom) throws Exception;

    void changeKing(ChatUser targetUser) throws Exception;
}
