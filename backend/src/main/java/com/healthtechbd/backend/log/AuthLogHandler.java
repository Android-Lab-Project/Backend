package com.healthtechbd.backend.log;

import com.healthtechbd.backend.dto.SignInDTO;
import com.healthtechbd.backend.dto.SignUpDTO;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Order(2)
public class AuthLogHandler {
    Logger logger  = LoggerFactory.getLogger(AuthLogHandler.class);

    @Before("execution(* com.healthtechbd.backend.controller.AuthController.authenticateAppUser(..))")
    public void beforeAuthenticateAppUser(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args.length > 0) {
            SignInDTO signInDTO = (SignInDTO) args[0];
            logger.info("Authenticating user with email: " + signInDTO.getEmail());
        }
    }

    @Before("execution(* com.healthtechbd.backend.controller.AuthController.registerAppUser(..))")
    public void beforeregisterAppUser(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        if (args.length > 0) {
            SignUpDTO signUpDTO = (SignUpDTO) args[0];
            logger.info("Registering user with email: " + signUpDTO.getEmail()+", name: "+signUpDTO.getFirstName()+" "+signUpDTO.getLastName());
        }
    }



}
