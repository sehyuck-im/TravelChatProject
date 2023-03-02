package com.TravelChat.common.config;

import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        HttpSession session = request.getSession();
        Integer mNo = (Integer) session.getAttribute("mNo");

        if(mNo != null){
            return true;
        }else{
            response.sendRedirect("/");
            return false;
        }
    }
}
