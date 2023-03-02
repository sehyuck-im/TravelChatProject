package com.TravelChat.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.web.firewall.DefaultHttpFirewall;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor()) //인터셉터 등록
                .order(1) //낮을 수록 먼저 호출
                .addPathPatterns("/**") //인터셉터를 적용할 url 패턴
                .excludePathPatterns("/download/**","/image/**","/bootstrap/**", "/*.ico", "/error", "/images/**", "/", "/register/**", "emailCode.html","/member/signIn"); //인터셉터에서 제외할 패턴 지정
    }

}
