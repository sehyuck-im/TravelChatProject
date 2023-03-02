package com.TravelChat.member.service;

import com.TravelChat.member.model.ShakeRequest;
import com.TravelChat.member.model.Shaker;

import java.util.List;

public interface ShakerService {
    // 해당 mNo이 shaker 가지고있는지 확인
    int countShakerKeyByMNo(int mNo);
    // 해당 mNo shake에 insert
    void insertMember(int mNo) throws Exception;

    Shaker selectByMNo(int mNo);

    void insertRequest(ShakeRequest shakeRequest) throws Exception;

    int countShakeRequestBySenderAndReceiver(ShakeRequest shakeRequest);

    int countShakeRequestByReversedOrder(ShakeRequest shakeRequest);
    // 받은 요청 리스트 보낸사람 사진, 닉네임 포함
    List<ShakeRequest> selectReceivedSakeRequestList(int mNo);
    // 받은 요청 count
    int countShakeRequestByShakeNo(int shakeNo);
    // shakeRequest select by shakeNo
    ShakeRequest selectShakeRequestByShakeNo(int shakeNo);
    // 친구 추가 후 shake request 삭제 tx
    void addShakerAndRemoveRequest(ShakeRequest shakeRequest) throws Exception;
    // 친구 추가
    int addShaker(int mNo, int targetNo) throws Exception;
    // shakeRequest 삭제
    int deleteShakeRequest(ShakeRequest shakeRequest) throws Exception;
    // shake request select
    ShakeRequest selectRequestBySenderAndReceiver(ShakeRequest shakeRequest);
    // shake request count
    int countShakeRequestByMNo(int mNo);
    // 친구 삭제, 각자 친구목록에서 삭제하기
    void goodByeToShaker(int target, int mNo) throws Exception;
    // 친구 삭제
    int removeShakers(int target, int mNo) throws Exception;
    // 친구 목록
    String getShakersNo(int mNo);
}
