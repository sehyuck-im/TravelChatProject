package com.TravelChat.member.service;

import com.TravelChat.member.model.ShakeRequest;
import com.TravelChat.member.model.Shaker;
import com.TravelChat.member.repository.ShakerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class ShakerServiceImpl implements ShakerService {
    @Autowired
    private ShakerRepository shakerRepository;

    @Override
    public int countShakerKeyByMNo(int mNo) {
        return shakerRepository.countShakerKeyByMNo(mNo);
    }

    @Override
    public void insertMember(int mNo) throws Exception {
        shakerRepository.insertMember(mNo);
    }

    @Override
    public Shaker selectByMNo(int mNo) {
        return shakerRepository.selectByMNo(mNo);
    }

    @Override
    public void insertRequest(ShakeRequest shakeRequest) throws Exception {
        shakerRepository.insertRequest(shakeRequest);
    }

    @Override
    public int countShakeRequestBySenderAndReceiver(ShakeRequest shakeRequest) {
        return shakerRepository.countShakeRequestBySenderAndReceiver(shakeRequest);
    }

    @Override
    public int countShakeRequestByReversedOrder(ShakeRequest shakeRequest) {
        return shakerRepository.countShakeRequestByReversedOrder(shakeRequest);
    }

    @Override
    public List<ShakeRequest> selectReceivedSakeRequestList(int mNo) {
        return shakerRepository.selectReceivedSakeRequestList(mNo);
    }

    @Override
    public int countShakeRequestByShakeNo(int shakeNo) {
        return shakerRepository.countShakeRequestByShakeNo(shakeNo);
    }

    @Override
    public ShakeRequest selectShakeRequestByShakeNo(int shakeNo) {
        return shakerRepository.selectShakeRequestByShakeNo(shakeNo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void addShakerAndRemoveRequest(ShakeRequest shakeRequest) throws Exception {
        // 친구 추가 두번 하기
        int addChk1 = addShaker(shakeRequest.getReceiver(), shakeRequest.getSender());
        int addChk2 = addShaker(shakeRequest.getSender(), shakeRequest.getReceiver());
        
        if(addChk1 != 1 || addChk2 != 1){
            throw new Exception("ADD_ERR");
        }
        // 요청 삭제하기
        int delChk = deleteShakeRequest(shakeRequest);
        if(delChk != 1){
            throw new Exception("DEL_ERR");
        }

    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public int addShaker(int mNo, int targetNo) throws Exception {

        Shaker shaker = setAddingShaker(mNo, targetNo);

        int chk = shakerRepository.addShaker(shaker);

        if(chk != 1){
            throw new Exception("ADD_ERR");
        }
        return chk;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public int deleteShakeRequest(ShakeRequest shakeRequest) throws Exception {
        int chk = shakerRepository.deleteShakeRequest(shakeRequest);

        if(chk != 1){
            throw new Exception("DEL_ERR");
        }
        return chk;
    }

    @Override
    public ShakeRequest selectRequestBySenderAndReceiver(ShakeRequest shakeRequest) {
        return shakerRepository.selectRequestBySenderAndReceiver(shakeRequest);
    }

    @Override
    public int countShakeRequestByMNo(int mNo) {
        return shakerRepository.countShakeRequestByMNo(mNo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void goodByeToShaker(int target, int mNo) throws Exception {
        int chk1 = removeShakers(target, mNo);
        int chk2 = removeShakers(mNo, target);

        if(chk1 != 1 || chk2 != 1){
            throw new Exception("REMOVE_ERR");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public int removeShakers(int targetNo, int mNo) throws Exception {
        Shaker shaker = selectByMNo(mNo);
        String shakerList = shaker.getShakers();
        String target = Integer.toString(targetNo);

        List<String> myShakers = new ArrayList<>(Arrays.asList(shakerList.split(",")));
        for (int i = 0; i < myShakers.size(); i++) {
            if (myShakers.get(i).equals(target)){
                myShakers.remove(myShakers.get(i));
            }
        }
        shakerList = "";
        for (int i = 0; i < myShakers.size(); i++) {
            if (i != myShakers.size() - 1) {
                shakerList += myShakers.get(i) + ",";
            } else {
                shakerList += myShakers.get(i);
            }
        }

        shaker.setShakers(shakerList);

        return shakerRepository.removeShakers(shaker);
    }

    @Override
    public String getShakersNo(int mNo) {
        return shakerRepository.getShakersNo(mNo);
    }


    private Shaker setAddingShaker(int mNo, int targetNo){
        Shaker tempShaker = new Shaker();
        String shakers = shakerRepository.getShakersNo(mNo);
        if(shakers == null){
            shakers = "none";
        }
        shakers.trim();
        if(shakers.equals("none") || shakers.isEmpty()){
            shakers = "";
            shakers += targetNo;
        }else{
            shakers += ","+targetNo;
        }

        tempShaker.setMNo(mNo);
        tempShaker.setShakers(shakers);

        return tempShaker;
    }
}
