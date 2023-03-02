package com.TravelChat.chat.service;

import com.TravelChat.chat.model.ChatHistory;
import com.TravelChat.chat.model.ChatRequest;
import com.TravelChat.chat.model.ChatRoom;
import com.TravelChat.chat.model.ChatUser;
import com.TravelChat.chat.repository.ChatHistoryRepository;
import com.TravelChat.chat.repository.ChatRepository;
import com.TravelChat.chat.repository.ChatRoomRepository;
import com.TravelChat.member.service.MemberService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@SpringBootTest
class ChatServiceImplTest {
    @Autowired
    ChatService chatService;
    @Autowired
    ChatRepository chatRepository;
    @Autowired
    ChatHistoryService chatHistoryService;
    @Autowired
    ChatUserService chatUserService;
    @Autowired
    MemberService memberService;

    @Autowired
    ChatRoomRepository chatRoomRepository;
    @Autowired
    ChatHistoryRepository chatHistoryRepository;

    @Test
    void 채팅기록가져오기() {
        // 1. 내가 어디까지 읽었는지 알려주는 chNo가져오기
        ChatUser chatUser = new ChatUser();
        chatUser.setUserNo(4);
        chatUser.setMNo(2);
        chatUser.setCrNo(2);
        chatUser.setChNo(72); // 내가 어디까지 읽었나


        int unreadCnt = chatHistoryService.getUnreadCnt(chatUser);
        System.out.println("unreadCnt = " + unreadCnt);
        List<ChatUser> userList = chatUserService.selectByCrNo(chatUser.getCrNo());

        for (int i = 0; i < userList.size(); i++) { // 닉네임 분리작업
            //nick # 분리
            String[] nickAndCode = memberService.separateNick(userList.get(i).getNick());
            userList.get(i).setNick(nickAndCode[0]);
            userList.get(i).setCode(nickAndCode[1]);
        }

        // 모든 메세지를 읽었을 때는 마지막 메세지 10개만
        if (unreadCnt == 0) {
            System.out.println("다읽은거라 10개만 가져와");
            ChatHistory chatHistoryCondition = new ChatHistory();
            chatHistoryCondition.setCrNo(chatUser.getCrNo());

            List<ChatHistory> chatHistoryList = chatHistoryService.selectLast10Msg(chatHistoryCondition);
            for (ChatHistory chatHistory : chatHistoryList) {
                if (chatHistory.getUserNo() == chatUser.getUserNo()) {
                    System.out.println("내가 쓴글");
                    System.out.println("chatHistory1.getChNo() = " + chatHistory.getChNo());
                    System.out.println("chatHistory1.getCrNo() = " + chatHistory.getCrNo());
                    System.out.println("chatHistory1.getUserNo() = " + chatHistory.getUserNo());
                    System.out.println("chatHistory1.getMessage() = " + chatHistory.getMessage());


                } else { // 다른사람이 쓴 글 일때
                    System.out.println("다른사람이 쓴 글");
                    System.out.println("chatHistory1.getChNo() = " + chatHistory.getChNo());
                    System.out.println("chatHistory1.getCrNo() = " + chatHistory.getCrNo());
                    System.out.println("chatHistory1.getUserNo() = " + chatHistory.getUserNo());
                    System.out.println("chatHistory1.getMessage() = " + chatHistory.getMessage());
                    for (ChatUser chatUser1 : userList) {
                        if (chatUser1.getUserNo() == chatHistory.getUserNo()) {
                            System.out.println("chatUser1.getNick() = " + chatUser1.getNick());
                            System.out.println("chatUser1.getCode() = " + chatUser1.getCode());
                            System.out.println("chatUser1.getPhoto() = " + chatUser1.getPhoto());
                            System.out.println("chatUser1.getMNo() = " + chatUser1.getMNo());
                            System.out.println("chatUser1.getUserNo() = " + chatUser1.getUserNo());
                        }
                    }

                }
                System.out.println("============================");
            }

        } else {

            ChatHistory chatHistory = new ChatHistory();
            chatHistory.setCrNo(chatUser.getCrNo());
            chatHistory.setChNo(chatUser.getChNo());
            chatHistory.setLimit(unreadCnt);
            // 안읽은 메세지가 있을 때는 안읽은 메세지 전부 담아서 보내기
            List<ChatHistory> chatHistoryList = chatHistoryService.selectUnreadMsg(chatHistory);
            // 안읽은 메세지 다 담은 후 여기까지 읽으셨습니다 붙여주기

            // 그리고 그 위에 메세지 10개 담아주기


            for (ChatHistory chatHistory1 : chatHistoryList) {
                System.out.println("다른사람이 쓴 글");
                System.out.println("chatHistory1.getChNo() = " + chatHistory1.getChNo());
                System.out.println("chatHistory1.getCrNo() = " + chatHistory1.getCrNo());
                System.out.println("chatHistory1.getUserNo() = " + chatHistory1.getUserNo());
                System.out.println("chatHistory1.getMessage() = " + chatHistory1.getMessage());
                for (ChatUser chatUser1 : userList) {
                    if (chatUser1.getUserNo() == chatHistory1.getUserNo()) {
                        System.out.println("chatUser1.getNick() = " + chatUser1.getNick());
                        System.out.println("chatUser1.getCode() = " + chatUser1.getCode());
                        System.out.println("chatUser1.getPhoto() = " + chatUser1.getPhoto());
                        System.out.println("chatUser1.getMNo() = " + chatUser1.getMNo());
                        System.out.println("chatUser1.getUserNo() = " + chatUser1.getUserNo());
                    }
                }

            }
            System.out.println("============================");

        }


    }

    @Test
    void addMockChatRequest() throws Exception {
        int receiver = 2;
        for (int i = 1; i <= 9; i++) {
            int sender = i;
            if (i != 2) {
                ChatRequest chatRequest = new ChatRequest();
                chatRequest.setReceiver(receiver);
                chatRequest.setSender(sender);
                chatService.createRequest(chatRequest);
            }

        }

    }

    @Test
    void mockChatHistoryInsert() {
        ChatHistory chatHistory = new ChatHistory();
        chatHistory.setCrNo(1);
        chatHistory.setUserNo(2);


    }

    @Test
    void crNoSelectTest() {
        ChatRoom testChatRoom = new ChatRoom();
        chatRoomRepository.createChatRoom(testChatRoom);
        System.out.println("createdCrNo = " + testChatRoom.getCrNo());

    }

    @Test
    void chNoSelectTest() {
        ChatHistory chatHistory = new ChatHistory();
        chatHistory.setCrNo(4);
        chatHistoryRepository.insertFirstMessage(chatHistory);
        System.out.println("chatHistory = " + chatHistory);

    }

    @Test
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    void createChatRoomAndDeleteRequest() throws Exception {
        ChatRequest chatRequest = chatService.selectChatRequest(9);
        // 1. 채팅방 생성
        ChatRoom chatRoom = createChatRoom();
        System.out.println("채팅방생성 완료");
        System.out.println("chatRoom = " + chatRoom);
        // 2. chat user 생성, 2명 생성해야함
        ChatUser chatUserReceiver = createChatUser(chatRoom, chatRequest.getReceiver());
        ChatUser chatUserSender = createChatUser(chatRoom, chatRequest.getSender());
        System.out.println("user생성완료");
        System.out.println("chatUserReceiver = " + chatUserReceiver);
        System.out.println("chatUserSender = " + chatUserSender);

        // chatUser userNo set, db에서 할 작업
        chatUserReceiver.setUserNo(11);
        chatUserSender.setUserNo(22);
        // 3. 채팅 요청 삭제
        chatRequestDel(chatRequest.getRequestNo());

        System.out.println("채팅방 생성 user생성 채팅요청 삭제 완료");

    }

    @Test
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    ChatRoom createChatRoom() throws Exception {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setCrNo(1);
        chatRoom.setCrTitle("");
        chatRoom.setCrDel("n");
//        throw new Exception("CREATE_ERR");
        return chatRoom;
    }

    @Test
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    ChatUser createChatUser(ChatRoom chatRoom, int mNo) throws Exception {
        ChatUser chatUser = new ChatUser();
        // chatUser userNo auto increase
        // chatUser mNo sender 와 receiver 둘로 나누기
        chatUser.setMNo(mNo);
        chatUser.setCrNo(chatRoom.getCrNo());
        return chatUser;
    }

    @Test
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    void chatRequestDel(int requestNo) throws Exception {
        chatRepository.deleteChatRequest(requestNo);
        System.out.println("request 삭제 완료");

    }

}