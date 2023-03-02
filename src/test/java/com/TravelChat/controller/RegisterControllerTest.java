package com.TravelChat.controller;

import com.TravelChat.common.service.EmailSendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
//@Transactional
class RegisterControllerTest {
    @Autowired
    EmailSendService es;


}
