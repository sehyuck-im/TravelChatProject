package com.TravelChat.member.service;

import com.TravelChat.member.model.ShakeRequest;
import com.TravelChat.member.model.Shaker;
import com.TravelChat.member.repository.ShakerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
class ShakerServiceImplTest {
    @Autowired
    private ShakerService shakerService;
    @Autowired
    private ShakerRepository shakerRepository;

    @Test
    @Transactional
    void shakerNullTest(){
        int mNo = 11;
        int target = 4;

        String shakers = shakerRepository.getShakersNo(mNo);
        System.out.println("shakers = " + shakers);
        if(shakers == null){
            System.out.println("null");
            shakers = "none";
        }
        System.out.println("shakers = " + shakers);
//        if(shakers.equals("none") || shakers.isEmpty()){
//
//            shakers = "";
//            shakers += target;
//        }else{
//
//            shakers += ","+target;
//        }
//        System.out.println("mNo shakers = " + shakers);
//        shakers = "";
//        shakers = shakerRepository.getShakersNo(target);
//        if(shakers.equals("none") || shakers.isEmpty() || shakers == null){
//
//            shakers = "";
//            shakers += mNo;
//        }else{
//
//            shakers += ","+mNo;
//        }
//
//        System.out.println("target shakers = " + shakers);
//

    }

    @Test
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    void addingShakerErrTest() {
        int mNo = 2;
        int targetNo = 1;

        ShakeRequest shakeRequest = new ShakeRequest();
        shakeRequest.setShakeNo(1);
        shakeRequest.setSender(targetNo);
        shakeRequest.setReceiver(mNo);
        try {
            shakerService.addShakerAndRemoveRequest(shakeRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Shaker sender = shakerService.selectByMNo(mNo);
        Shaker receiver = shakerService.selectByMNo(targetNo);
        System.out.println("sender = " + sender);
        System.out.println("receiver = " + receiver);
    }

    @Test
    void deleteErrTx() {
        int mNo = 2;
        int targetNo = 1;

        ShakeRequest shakeRequest = new ShakeRequest();
        shakeRequest.setShakeNo(1);
        shakeRequest.setSender(targetNo);
        shakeRequest.setReceiver(mNo);
        try {
            shakerService.addShakerAndRemoveRequest(shakeRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Shaker sender = shakerService.selectByMNo(mNo);
        Shaker receiver = shakerService.selectByMNo(targetNo);
        ShakeRequest shakeRequest1 = shakerService.selectShakeRequestByShakeNo(1);
        System.out.println("sender = " + sender);
        System.out.println("receiver = " + receiver);
        System.out.println("shakeRequest1 = " + shakeRequest1);
    }

    @Test
    void removeShakerTest() {
        String data = "3,4,5,6,7";
        List<String> myShakers = new ArrayList<>(Arrays.asList(data.split(",")));
        System.out.println("myShakers = " + myShakers);
        System.out.println("myShakers.size() = " + myShakers.size());

        String target = "5";
        for (int i = 0; i < myShakers.size(); i++) {
            if (myShakers.get(i).equals(target)){
                System.out.println("일치");
                myShakers.remove(myShakers.get(i));
            }
        }

        data = "";
        for (int i = 0; i < myShakers.size(); i++) {
            if (i != myShakers.size() - 1) {
                data += myShakers.get(i) + ",";
            } else {
                data += myShakers.get(i);
            }
        }

        System.out.println("data = " + data);

    }
    @Test
    @Transactional
    void goodByeToShakerTestWithTx(){
        int mNo = 2;
        int target = 1;

//        try {
//            removeTestWithTx(target, mNo);
//        } catch (Exception e) {
//            e.printStackTrace();
//            System.out.println("e.getMessage() = " + e.getMessage());
//        }

        try {
            shakerService.goodByeToShaker(target, mNo);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("e.getMessage() = " + e.getMessage());
        }

        Shaker shaker = shakerService.selectByMNo(mNo);
        Shaker shaker1 = shakerService.selectByMNo(target);

        System.out.println("shaker = " + shaker);
        System.out.println("shaker1 = " + shaker1);


    }

    @Test
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    void removeTestWithTx(int target, int mNo) throws Exception {
        int chk1 = removeShakers(target, mNo);
        int chk2 = removeShakers(mNo, target);


//        if(chk1 != 1 || chk2 != 1){
//            throw new Exception("TXERR");
//        }
    }

    @Test
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    int removeShakers(int target, int mNo) throws Exception {
        Shaker shaker = shakerService.selectByMNo(mNo);
        String shakerList = shaker.getShakers();
        List<String> myShakers = new ArrayList<>(Arrays.asList(shakerList.split(",")));
        for (int i = 0; i < myShakers.size(); i++) {
            if (myShakers.get(i).equals(Integer.toString(target))){
                System.out.println("삭제");
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
        if(target == 2){
            throw new Exception("ERR");
        }
        return shakerRepository.removeShakers(shaker);
    }


}