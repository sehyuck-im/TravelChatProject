package com.TravelChat.member.controller;

import com.TravelChat.common.Util.UtilService;
import com.TravelChat.common.model.Email;
import com.TravelChat.common.model.EmailCode;
import com.TravelChat.member.model.Member;
import com.TravelChat.common.service.EmailSendService;
import com.TravelChat.member.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/register")
public class RegisterController {

    @Autowired
    private EmailSendService es;
    @Autowired
    private MemberService ms;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UtilService utilService;


    @GetMapping("/emailChk")
    public String emailChk(EmailCode emailCode, Model m) {
        m.addAttribute("emailCode", emailCode);
        return "register/emailChk";
    }

    @PostMapping("/registerForm")
    public String registerForm(EmailCode emailCode, Model m, RedirectAttributes rattr) {

        EmailCode tempEmailCode = es.selectEmailCode(emailCode.getEmail());

        if (!emailCode.equals(tempEmailCode)) {

            String msg = "CHK_ERR";
            rattr.addFlashAttribute("msg", msg);
            rattr.addFlashAttribute("emailCode", emailCode);

            return "redirect:/register/emailChk";

        } else {
            m.addAttribute("emailCode", emailCode);
            return "register/registerForm";
        }

    }

    @GetMapping("/emailChkBtn")
    @ResponseBody
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public String mailChk(@RequestParam("email") String to) throws Exception {
        int idCnt = ms.idSelect(to);
        if (idCnt != 0) {
            return "DUP";
        }
        Email email = new Email();
        email.setTo(to);
        // 6?????? ?????? ??????
        email.setCode(utilService.ranNum(6));
        // emailcode ??????
        email = es.sendEmailCode(email);
        if (email.getReason().equals("INVALID_EMAIL") || email.getReason().equals("ERROR")) {
            return email.getReason();
        } else {
            // emailCode db??? upsert
            EmailCode emailCode = new EmailCode(email.getTo(), email.getCode());
            int result = es.upsert(emailCode);
            // 1?????? insert, 2??? update
            if (result != 1 && result != 2) {
                // ?????? ?????? ?????? roll back
                email.setReason("DB_ERR");
            }
        }
        return email.getReason();
    }

    //email code ajax key up response
    @GetMapping("/emailCodeCheck")
    @ResponseBody
    public String emailCodeCheck(@RequestParam("inputCode") String inputCode, @RequestParam("code") String code) {
        String msg = "error";

        if (passwordEncoder.matches(inputCode, code)) {
            msg = "OK";

        } else {
            msg = "FAIL";
        }
        return msg;
    }

    @PostMapping("/signup")
    public String signup(Model m, Member member, RedirectAttributes rattr) {
        // 1. nick ?????? #0000 ?????? ???????????? ????????????
        String nick = ms.addNick(member.getNick());
        member.setNick(nick);
        // 2. ?????? encode ?????????
        String pwd = passwordEncoder.encode(member.getPassword());
        member.setPassword(pwd);
        // 3. member insert and emailCode delete

        try {
            ms.insertAndDelete(member);
            rattr.addFlashAttribute("msg", 1);
            return "redirect:/";
        } catch (Exception e) {
            String msg = "";
            if (e.getMessage().equals("INS_ERR")) {
                EmailCode emailCode = new EmailCode();
                emailCode.setEmail(member.getEmail());
                msg = e.getMessage();
                rattr.addFlashAttribute("msg", msg);
                rattr.addFlashAttribute("emailCode", emailCode);
                return "redirect:/register/registerForm";

            } else if (e.getMessage().equals("DEL_ERR")) {

                EmailCode emailCode = new EmailCode();
                emailCode.setEmail(member.getEmail());
                msg = e.getMessage();
                rattr.addFlashAttribute("msg", msg);
                rattr.addFlashAttribute("emailCode", emailCode);
                return "redirect:/register/emailChk";
            } else {
                // ????????? ??????
                msg = "SER_ERR";
                rattr.addFlashAttribute("msg", msg);
                return "redirect:/register/emailChk";
            }
        }


    }

    @GetMapping("/forgotInfo")
    public String forgotInfo() {

        return "register/forgotInfo";
    }

    @PostMapping("/sendNewPass")
    @ResponseBody
    public String sendNewPass(@RequestBody String email) {
        int count = ms.countMemberByEmail(email);
        if (count == 0) {

            return "NOT_MEMBER";
        }
        // 1. membr select
        Member member = ms.selectById(email);
        // 2. 10?????? ????????? ??????+????????? ???????????? ??????
        String newPass = utilService.ranNumAndAlphabet(10);
        member.setPassword(passwordEncoder.encode(newPass));
        // 3. ????????? ?????? + ???????????? ??????
        try {
            ms.updatePassAndSendEmail(member, newPass);
            return "SUCCESS";
        } catch (Exception e) {
            e.printStackTrace();
            return "FAIL";
        }
    }

}
