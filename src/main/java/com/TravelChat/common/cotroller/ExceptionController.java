package com.TravelChat.common.cotroller;


import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.server.MethodNotAllowedException;

@ControllerAdvice
public class ExceptionController {

    @ExceptionHandler({HttpClientErrorException.NotFound.class,
            HttpClientErrorException.BadRequest.class,
            MethodNotAllowedException.class,
            HttpClientErrorException.Unauthorized.class,
            HttpClientErrorException.Forbidden.class
    })
    public String catcher400(){

        return "error/400";
    }
    @ExceptionHandler(HttpServerErrorException.InternalServerError.class)
    public String catcher500(){

        return "error/500";
    }
}
