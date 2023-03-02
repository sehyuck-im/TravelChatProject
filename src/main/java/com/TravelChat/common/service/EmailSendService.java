package com.TravelChat.common.service;

import com.TravelChat.common.model.Email;
import com.TravelChat.common.model.EmailCode;
import com.TravelChat.member.model.Member;

public interface EmailSendService {
    Email sendEmailCode(Email email);
    int upsert(EmailCode emailCode);
    EmailCode selectEmailCode(String email);
    int delete(String email) throws Exception;

    void sendNewPassEmail(Member member, String newPass) throws Exception;

    void sendSuspendEmail(Member member);
}
