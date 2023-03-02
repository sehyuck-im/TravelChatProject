package com.TravelChat.chat.service;

import com.TravelChat.chat.model.ChatRoom;
import com.TravelChat.chat.repository.ChatRoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ChatRoomServiceImpl implements ChatRoomService {
    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public int createChatRoom() throws Exception {

        ChatRoom createdChatRoom = new ChatRoom();
        try {
            chatRoomRepository.createChatRoom(createdChatRoom);

        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("CHATROOM_INSERT_ERR");
        }

        return createdChatRoom.getCrNo();
    }

    @Override
    public ChatRoom selectChatRoomByCrNo(int crNo) {
        return chatRoomRepository.selectChatRoomByCrNo(crNo);
    }

    @Override
    public void updateTimeAtChatRoom(int crNo) {
        chatRoomRepository.updateTimeAtChatRoom(crNo);
    }

    @Override
    public int selectAdmin(int crNo) {
        return chatRoomRepository.selectAdmin(crNo);
    }

    @Override
    public void updateCrTitle(ChatRoom chatRoom) throws Exception{
        chatRoomRepository.updateCrTitle(chatRoom);
    }
}
