package com.TravelChat.member.repository;

import com.TravelChat.member.model.ShakeRequest;
import com.TravelChat.member.model.Shaker;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface ShakerRepository {

    int countShakerKeyByMNo(int mNo);

    void insertMember(int mNo);

    Shaker selectByMNo(int mNo);

    void insertRequest(ShakeRequest shakeRequest);

    int countShakeRequestBySenderAndReceiver(ShakeRequest shakeRequest);

    int countShakeRequestByReversedOrder(ShakeRequest shakeRequest);

    List<ShakeRequest> selectReceivedSakeRequestList(int mNo);

    int countShakeRequestByShakeNo(int shakeNo);

    ShakeRequest selectShakeRequestByShakeNo(int shakeNo);

    int addShaker(Shaker tempShaker);

    String getShakersNo(int mNo);

    int deleteShakeRequest(ShakeRequest shakeRequest);

    ShakeRequest selectRequestBySenderAndReceiver(ShakeRequest shakeRequest);

    int countShakeRequestByMNo(int mNo);

    int removeShakers(Shaker shaker);

}
