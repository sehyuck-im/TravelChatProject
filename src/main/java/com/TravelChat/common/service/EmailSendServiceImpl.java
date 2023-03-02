package com.TravelChat.common.service;

import com.TravelChat.common.model.Email;
import com.TravelChat.common.model.EmailCode;
import com.TravelChat.member.model.Member;
import com.TravelChat.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import javax.mail.internet.MimeMessage;


@Service
public class EmailSendServiceImpl implements EmailSendService {
    @Autowired
    private JavaMailSender emailSender;
    @Autowired
    private SpringTemplateEngine templateEngine;
    @Autowired
    private MemberRepository mr;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Override
    public Email sendEmailCode(Email email) {

        String title = "Are you ready to travel?🔥  ";
        String content = email.getCode();

        email.setTitle(title);
        email.setContent(content);

        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            //메일 제목 설정
            helper.setSubject(email.getTitle());
            //수신자
            helper.setTo(email.getTo());

            //템플릿에 전달할 데이터 설정
            Context context = new Context();
            context.setVariable("content", content);
            // 사용할 template 이름, context 담기
            String html = templateEngine.process("emailCode", context);
            helper.setText(html, true);
            // template에 들어가는 이미지를 cid에 맞춰서 삽입
            helper.addInline("image1", new ClassPathResource("static/images/emailCode1.png"));
            helper.addInline("image2", new ClassPathResource("static/images/emailCode2.png"));

            emailSender.send(message);
            // 완료 후 enCoding된 code set 해주기
            String enCode = passwordEncoder.encode(content);
            email.setReason(enCode);



        } catch (MailException e) {
            e.printStackTrace();
            email.setReason("INVALID_EMAIL");


        } catch (Exception e) {
            e.printStackTrace();
            email.setReason("ERROR");

        }

        return email;
    }

    @Override
    public int upsert(EmailCode emailCode) {
        int result = mr.upsertEmailCode(emailCode);

        return result;
    }

    @Override
    public EmailCode selectEmailCode(String email) {

        return mr.selectEmailCode(email);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public int delete(String email) throws Exception {

//            throw new Exception("DEL_ERR");

        return mr.deleteEmailCode(email);
    }

    @Override
    public void sendNewPassEmail(Member member, String newPass) throws Exception {
        String title = "Your Password has been changed!";
        String content = newPass;

        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            //메일 제목 설정
            helper.setSubject(title);
            //수신자
            helper.setTo(member.getEmail());

            //템플릿에 전달할 데이터 설정
            Context context = new Context();
            context.setVariable("content", content);
            // 사용할 template 이름, context 담기
            String html = templateEngine.process("newPassEmail", context);
            helper.setText(html, true);
            // template에 들어가는 이미지를 cid에 맞춰서 삽입
            helper.addInline("image1", new ClassPathResource("static/images/emailCode1.png"));
            helper.addInline("image2", new ClassPathResource("static/images/emailCode2.png"));

            emailSender.send(message);


        } catch (MailException e) {
            e.printStackTrace();

        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    @Override
    public void sendSuspendEmail(Member member) {
        String title = "Your account has been suspended";
        String content = "관리자에 의해 계정이 정지되었습니다. 자세한 사항은 travelPlannerOfficial@gmail.com으로 문의 바랍니다.";

        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            //메일 제목 설정
            helper.setSubject(title);
            //수신자
            helper.setTo(member.getEmail());

            //템플릿에 전달할 데이터 설정
            Context context = new Context();
            context.setVariable("content", content);
            // 사용할 template 이름, context 담기
            String html = templateEngine.process("emailForm", context);
            helper.setText(html, true);
            // template에 들어가는 이미지를 cid에 맞춰서 삽입
            helper.addInline("image1", new ClassPathResource("static/images/emailCode1.png"));
            helper.addInline("image2", new ClassPathResource("static/images/emailCode2.png"));

            emailSender.send(message);


        } catch (MailException e) {
            e.printStackTrace();

        } catch (Exception e) {
            e.printStackTrace();

        }
    }
}
