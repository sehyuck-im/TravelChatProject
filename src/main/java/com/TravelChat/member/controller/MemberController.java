package com.TravelChat.member.controller;

import com.TravelChat.common.model.Comment;
import com.TravelChat.common.model.Feed;
import com.TravelChat.common.service.CommentService;
import com.TravelChat.common.service.FeedService;
import com.TravelChat.common.service.PhotoService;
import com.TravelChat.member.model.Member;
import com.TravelChat.member.model.Profile;
import com.TravelChat.member.model.ShakeRequest;
import com.TravelChat.member.model.Shaker;
import com.TravelChat.member.service.MemberService;
import com.TravelChat.member.service.ShakerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.rmi.ServerError;
import java.util.ArrayList;
import java.util.List;


@Controller
@RequestMapping("/member")
public class MemberController {
    @Autowired
    private MemberService ms;
    @Autowired
    private HttpSession session;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ShakerService shakerService;
    @Autowired
    private PhotoService photoService;
    @Autowired
    private FeedService feedService;
    @Autowired
    private CommentService commentService;


    @PostMapping("/signIn")
    public String signIn(Member member, Model m, RedirectAttributes rattr) {
        Member tempMember = ms.selectById(member.getEmail());

        if (tempMember == null) { // 일치하는 아이디가 없을 경우
            rattr.addFlashAttribute("msg", "NOT_MEMBER");
            rattr.addFlashAttribute("member", member);
            return "redirect:/";
        }
        if (tempMember.getStatus().equals("s")) { // 정지된 아이디
            rattr.addFlashAttribute("msg", "SUSPEND_MEMBER");
            rattr.addFlashAttribute("member", member);
            return "redirect:/";
        }
        if (!passwordEncoder.matches(member.getPassword(), tempMember.getPassword())) {
            // 아이디는 일치, 비밀번호 불일치
            rattr.addFlashAttribute("msg", "INCORRECT_PASS");
            rattr.addFlashAttribute("member", member);
            return "redirect:/";
        }

        m.addAttribute("member", tempMember);

        // 세션 객체에 ID를 저장
        session.setAttribute("mNo", tempMember.getMNo());

        return "redirect:/main";

    }

    @GetMapping("/myProfile")
    public String myProfile(Member member, Model model, Profile profile) {
        int mNo = (int) session.getAttribute("mNo");
        member = ms.selectByMno(mNo);
        int cnt = ms.countProfileByMno(mNo);
        if (cnt == 0) {
            ms.insertProfile(mNo);
        }
        profile = ms.selectProfileByMno(mNo);
        //nick # 분리
        String[] nickAndCode = ms.separateNick(member.getNick());
        String code = nickAndCode[1];
        member.setNick(nickAndCode[0]);

        model.addAttribute("member", member);
        model.addAttribute("code", code);
        model.addAttribute("profile", profile);

        return "/member/myProfile";
    }

    @PostMapping("/myPhoto")
    @ResponseBody
    public ResponseEntity<?> myPhoto(@RequestParam("photo") MultipartFile photo) {
        int mNo = (int) session.getAttribute("mNo");
        Member member = ms.selectByMno(mNo);
        Profile profile = ms.selectProfileByMno(member.getMNo());
        String result = "";
        if (photo.isEmpty()) {

            return new ResponseEntity<>("NO_FILE", HttpStatus.OK);
        }
        String path = "aws 경로"; //aws에 올리면 변경해야함
//        path = "C:/soloProject/image/profile";
        path = "/var/lib/tomcat9/webapps/image/profile"; // 현재 내컴에 경로 저장
        try {
            result = photoService.savePhoto(photo, profile, path);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.OK);
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/updateProfile")
    public String updateProfile(String nick, Profile temp, RedirectAttributes rattr) {

        int mNo = (int) session.getAttribute("mNo");
        Member member = ms.selectByMno(mNo);
        if (temp.getIntro().equals("자기소개가 없습니다")) {
            temp.setIntro("");
        }
        String newNick = "";

        Profile profile = ms.selectProfileByMno(member.getMNo());
        profile.setOpenProfile(temp.getOpenProfile());
        profile.setOpenGender(temp.getOpenGender());
        profile.setIntro(temp.getIntro());

        String[] nickAndCode = ms.separateNick(member.getNick());
        if (!nickAndCode[0].equals(nick)) {
            // 닉네임을 변경한 경우
            newNick = ms.addNick(nick);
        } else {
            newNick = member.getNick();
        }
        Member nickMember = new Member();
        nickMember.setNick(newNick);
        nickMember.setMNo(member.getMNo());


        String msg = "";
        try {
            ms.updateProfile(nickMember, profile);
        } catch (Exception e) {
            if (e.getMessage().equals("NICK_ERR")) {
                msg = e.getMessage();
            } else if (e.getMessage().equals("PROFILE_ERR")) {
                msg = e.getMessage();
            } else {
                // 그외의 에러
                msg = "SER_ERR";
            }
            rattr.addFlashAttribute("msg", msg);
            return "redirect:/member/myProfile";
        }

        rattr.addFlashAttribute("msg", "SUCCESS");
        return "redirect:/member/myProfile";
    }

    @GetMapping("/passwordChangeForm")
    public String passwordChangeForm() {
        return "/member/passwordChangeForm";
    }

    @PostMapping("/passwordChange")
    public String passwordChange(String newPassword, String currentPassword, RedirectAttributes rattr) {
        int mNo = (int) session.getAttribute("mNo");
        Member member = ms.selectByMno(mNo);
        String msg = "";

        // 현재 비번이 다르면 false
        if (!passwordEncoder.matches(currentPassword, member.getPassword())) {
            msg = "CURRENT_INCORRECT";
            rattr.addFlashAttribute("msg", msg);
            return "redirect:/member/passwordChangeForm";
        }

        String encodeNew = passwordEncoder.encode(newPassword);
        member.setPassword(encodeNew);

        try {
            ms.updatePassword(member);
        } catch (Exception e) {
            e.printStackTrace();

            msg = "CHANGE_FAILED";
            rattr.addFlashAttribute("msg", msg);
            return "redirect:/member/passwordChangeForm";
        }

        msg = "PASS_SUCCESS";
        rattr.addFlashAttribute("msg", msg);
        return "redirect:/member/myProfile";
    }

    @GetMapping("leaveForm")
    public String leaveForm() {

        return "/member/leaveForm";
    }

    @PostMapping("leaveMember")
    public String leaveMember(String password, RedirectAttributes rattr) {
        int mNo = (int) session.getAttribute("mNo");
        Member member = ms.selectByMno(mNo);
        String msg = "";
        if (!passwordEncoder.matches(password, member.getPassword())) {
            msg = "PASSWORD_INCORRECT";
            rattr.addFlashAttribute("msg", msg);
            return "redirect:/member/leaveForm";
        }
        try {
            ms.leaveMember(member);
        } catch (Exception e) {

            if (e.getMessage().equals("INSERT_ERR") || e.getMessage().equals("DELM_ERR")
                    || e.getMessage().equals("DELP_ERR")) {
                msg = "DEL_FAILED";
            } else {
                msg = "SER_ERROR";
            }
            rattr.addFlashAttribute("msg", msg);
            return "redirect:/member/leaveForm";
        }

        msg = "DEL_SUCCESS";
        rattr.addFlashAttribute("msg", msg);
        return "redirect:/";
    }

    @GetMapping("/signOut")
    public String signOut(RedirectAttributes rattr) {

        session.invalidate();
        rattr.addFlashAttribute("msg", "SIGNOUT");
        return "redirect:/";
    }

    @GetMapping("/info")
    public String userInfo(String mNo, Model model) {
        int readerNo = (int) session.getAttribute("mNo");
        Member reader = ms.selectByMno(readerNo);

        int memberNumber = Integer.parseInt(mNo);
        if (memberNumber == reader.getMNo()) {
            // 내정보를 보려고 하는 경우 마이 프로필로 리다이렉트
            return "redirect:/member/myProfile";
        }
        int chk = ms.countMemberByMNo(memberNumber);
        if (chk == 0) {
            model.addAttribute("result", 1);
            return "/member/notFound";
        }
        Member targetMember = ms.selectByMno(memberNumber);
        Profile targetProfile = ms.selectProfileByMno(memberNumber);
        // nick code 분리


        String[] nickAndCode = ms.separateNick(targetMember.getNick());
        String code = nickAndCode[1];
        targetMember.setNick(nickAndCode[0]);
        targetMember.setCode(code);

        // db에서 shaker key가 있는지 확인
        int cnt = shakerService.countShakerKeyByMNo(readerNo);
        Shaker shaker = new Shaker();
        String isShaker = "n";
        if (cnt == 0) { // 해당 멤버는 shaker가 없음
            isShaker = "n";
            try {
                shakerService.insertMember(readerNo);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // shaker db에 존재함, 친구 인지 아닌지 확인,
            shaker = shakerService.selectByMNo(readerNo);
            shaker.setShakers(shaker.getShakers().trim());
            if (shaker.getShakers() == null || shaker.getShakers().equals("")) {
                shaker.setShakers("none");
            }
            if (!shaker.getShakers().trim().isEmpty() && !shaker.getShakers().equals("none")) {
                System.out.println("shaker = " + shaker);
                String[] myShakers = shaker.getShakers().split(",");
                if (myShakers.length > 0) { // 이미 친구인지 확인
                    for (String myShaker : myShakers) {
                        if (Integer.parseInt(myShaker) == memberNumber) {
                            isShaker = "y";
                        }
                    }
                }
            } else {
                isShaker = "n";
            }
            // 친구요청을 보낸적이 있는지 확인
            ShakeRequest shakeRequest = new ShakeRequest();
            shakeRequest.setSender(readerNo);
            shakeRequest.setReceiver(memberNumber);
            int chk1 = shakerService.countShakeRequestBySenderAndReceiver(shakeRequest);
            int chk2 = shakerService.countShakeRequestByReversedOrder(shakeRequest);
            if (chk1 > 0 || chk2 > 0) { // 요청이 존재, 아직 수락은 안함
                isShaker = "n";
            }

        }
        // 최초에 가장 최근 5개만 불러오기
        List<Feed> targetFeedList = feedService.selectRecentFeedListByMNo(targetMember.getMNo());
        for (Feed feed : targetFeedList) {
            // feed 사진 set
            String[] photoArr = feed.getPhoto().split(",");
            if (photoArr.length > 1) { // 사진이 두장 이상이면
                for (String tempPhoto : photoArr) {
                    feed.getPhotoNames().add(tempPhoto);
                }
            }
            // 댓글 set
            List<Comment> commentList = commentService.selectCommentListByFeedNo(feed.getFeedNo());
            for (Comment comment : commentList) { // nick code 분리
                String[] CommenterNickAndCode = ms.separateNick(comment.getNick());
                comment.setNick(CommenterNickAndCode[0]);
                comment.setCode(CommenterNickAndCode[1]);
            }
            feed.setCommentList(commentList);
        }
        int feedCount = feedService.countAllFeedByWriter(targetMember.getMNo());
        model.addAttribute("feedCount", feedCount);
        model.addAttribute("readerNo", readerNo);
        model.addAttribute("isShaker", isShaker);
        model.addAttribute("code", code);
        model.addAttribute("targetMember", targetMember);
        model.addAttribute("targetProfile", targetProfile);
        model.addAttribute("targetFeedList", targetFeedList);

        return "/member/info";
    }

    @GetMapping("/requestShake")
    @ResponseBody
    public String requestChat(@RequestParam("receiver") String receiver) {
        int mNo = (int) session.getAttribute("mNo");
        Member member = ms.selectByMno(mNo);
        int sender = member.getMNo();
        int receiverMNo = Integer.parseInt(receiver);

        // db에서 shaker key가 있는지 확인
        int cnt = shakerService.countShakerKeyByMNo(sender);
        Shaker shaker = new Shaker();
        if (cnt == 0) { // 해당 멤버는 shaker가 없음
            try {
                shakerService.insertMember(sender);
            } catch (Exception e) {
                return "CREAT_ERR";
            }
            shaker = shakerService.selectByMNo(mNo);
        } else {
            shaker = shakerService.selectByMNo(mNo);
            String[] myShakers = shaker.getShakers().split(",");
            if (myShakers.length > 0) { // 이미 친구인지 확인

            }
        }

        ShakeRequest shakeRequest = new ShakeRequest();
        shakeRequest.setSender(sender);
        shakeRequest.setReceiver(receiverMNo);
        // sender receiver
        int chk1 = shakerService.countShakeRequestBySenderAndReceiver(shakeRequest);
        int chk2 = shakerService.countShakeRequestByReversedOrder(shakeRequest);

        if (chk1 != 0) {
            return "WAIT_RESPONSE"; // 응답을 기다리는 중
        } else if (chk2 != 0) {
            return "CHK_REQUEST"; // 상대방이 이미 보냄
        }

        try {
            shakerService.insertRequest(shakeRequest);
        } catch (Exception e) {

            return "INSERT_ERR";
        }

        return "SUCCESS_SHAKEREQ";
    }

    @GetMapping("/shakeRequestList")
    public String shakeRequestList(Model model) {
        int mNo = (int) session.getAttribute("mNo");
        Member member = ms.selectByMno(mNo);
        // db에서 shaker key가 있는지 확인
        int cnt = shakerService.countShakerKeyByMNo(mNo);

        if (cnt == 0) { // 해당 멤버는 shaker가 없음
            try {
                shakerService.insertMember(mNo);
            } catch (Exception e) {
                System.out.println("e.getMessage() = " + e.getMessage());
            } finally {
                List<ShakeRequest> shakeRequestList = new ArrayList<>();
                model.addAttribute("shakeRequestList", shakeRequestList);
                return "/requests/shakeRequestList";
            }
        }

        // 1. 해당 멤버가 받은 요청 리스트
        List<ShakeRequest> shakeRequestList = shakerService.selectReceivedSakeRequestList(mNo);

        // 2. nick code 분리
        for (ShakeRequest shakeRequest : shakeRequestList) {
            Member senderMember = ms.selectByMno(shakeRequest.getSender());
            String senderPhoto = ms.selectPhotoByMno(shakeRequest.getSender());
            String[] nickAndCode = ms.separateNick(senderMember.getNick());
            shakeRequest.setNick(nickAndCode[0]);
            shakeRequest.setCode(nickAndCode[1]);
            shakeRequest.setPhoto(senderPhoto);
        }

        model.addAttribute("shakeRequestList", shakeRequestList);

        return "/requests/shakeRequestList";
    }

    @GetMapping("/responseShakeRequest")
    @ResponseBody
    public String responseRequest(@RequestParam("shakeNo") int shakeNo, @RequestParam("type") String type) {
        // 1. 조회, null 일경우 0 반환
        int requestCount = shakerService.countShakeRequestByShakeNo(shakeNo);

        // 2. 해당하는 data 있으면 동작
        if (requestCount != 0) {
            // type에 따라 수락인지 거절인지 분기
            ShakeRequest shakeRequest = shakerService.selectShakeRequestByShakeNo(shakeNo);
            // 친구인지 확인
            Shaker shaker = shakerService.selectByMNo(shakeRequest.getReceiver());
            String[] myShakers = shaker.getShakers().split(",");
            for (String temp : myShakers) {
                if (temp.equals(Integer.toString(shakeRequest.getSender()))) {
                    return "ALREADY";
                }
            }
            if (type.equals("accept")) {
                try {
                    shakerService.addShakerAndRemoveRequest(shakeRequest);
                } catch (Exception e) {
                    e.printStackTrace();
                    if (e.getMessage().equals("ADD_ERR") || e.getMessage().equals("DEL_ERR")) {
                        return "RESPONSE_ERR";
                    } else {
                        return "UNEXPECTED_ERR";
                    }
                }
                // 수락, 거절 했으니 헤더 알람에서 카운트 --
                return "ACCEPT";
            } else { // 거절일 경우 요청만 삭제

                try {
                    shakerService.deleteShakeRequest(shakeRequest);
                } catch (Exception e) {
                    e.printStackTrace();

                    return "DELETE_ERR";
                }
                // 수락, 거절 했으니 헤더 알람에서 카운트 --
                return "DECLINE";
            }
        } else {
            return "REQUEST_NOT_FOUND";
        }
    }

    @GetMapping("/goodBye")
    @ResponseBody
    public String goodBye(@RequestParam("target") int target) {
        int mNo = (int) session.getAttribute("mNo");

        // 1. shaker 있는지 조회
        int shakerChk = shakerService.countShakerKeyByMNo(mNo);
        if (shakerChk == 0) {
            return "NOSHAKER";
        } else {
            // 2. 친구인지 조회
            String targetNo = Integer.toString(target);
            Shaker shaker = shakerService.selectByMNo(mNo);
            String[] myShakers = shaker.getShakers().split(",");
            for (String myShaker : myShakers) { // 친구인지 확인
                if (myShaker.equals(targetNo)) {
                    try {
                        shakerService.goodByeToShaker(target, mNo);
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (e.getMessage().equals("DEL_ERR")) {
                            return "DEL_ERR";
                        } else {
                            return "UNEXPECTED_ERR";
                        }
                    }
                    return "SUCCESS";
                }
            }
            return "NOTSHAKER"; // 친구목록에 없음
        }
    }

    @GetMapping("/myShakers")
    public String myShakerList(Model model) {
        int mNo = (int) session.getAttribute("mNo");
        // 1. shaker 있는지 조회
        int shakerChk = shakerService.countShakerKeyByMNo(mNo);
        if (shakerChk == 0) {
            model.addAttribute("shakerCount", 0);
        } else {
            String shakers = shakerService.getShakersNo(mNo);
            shakers = shakers.trim();
            if (!shakers.isEmpty() && !shakers.equals("none")) {
                List<Member> memberList = new ArrayList<>();
                String[] myShakers = shakers.split(",");
                for (String shaker : myShakers) {
                    Member shakerInfo = ms.selectByMno(Integer.parseInt(shaker));
                    shakerInfo.setPhoto(ms.selectPhotoByMno(Integer.parseInt(shaker)));
                    String[] nickAndCode = ms.separateNick(shakerInfo.getNick());
                    shakerInfo.setNick(nickAndCode[0]);
                    shakerInfo.setCode(nickAndCode[1]);
                    memberList.add(shakerInfo);
                }
                int shakerCount = myShakers.length;
                model.addAttribute("memberList", memberList);
                model.addAttribute("shakerCount", shakerCount);
            } else {
                model.addAttribute("shakerCount", 0);
            }
        }
        return "/member/myShakers";
    }
}
