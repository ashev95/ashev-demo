package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationFailureBadCredentialsEventListener implements ApplicationListener<AuthenticationFailureBadCredentialsEvent> {

    @Autowired
    LoginAttemptService loginAttemptService;

    @Override
    public void onApplicationEvent(final AuthenticationFailureBadCredentialsEvent e) {
        loginAttemptService.onFail(e.getAuthentication().getPrincipal().toString());
    }
}