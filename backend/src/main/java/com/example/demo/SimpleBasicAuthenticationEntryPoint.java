package com.example.demo;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Component
public class SimpleBasicAuthenticationEntryPoint extends BasicAuthenticationEntryPoint {

    private static final AccessTemporarilyRestrictedException ACCESS_TEMPORARILY_RESTRICTED_EXCEPTION = new AccessTemporarilyRestrictedException();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException {
        // не нужно отображать окно с вводом логина-пароля при неверных или пустых данных
        //response.addHeader("WWW-Authenticate", "Basic realm=" + getRealmName() + "");
        if (ACCESS_TEMPORARILY_RESTRICTED_EXCEPTION.getMessage().equals(e.getMessage())) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            PrintWriter writer = response.getWriter();
            writer.println("HTTP Status 403 - " + e.getMessage());
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            PrintWriter writer = response.getWriter();
            writer.println("HTTP Status 401 - " + e.getMessage());
        }
    }

    @Override
    public void afterPropertiesSet() {
        setRealmName("Realm");
        super.afterPropertiesSet();
    }
}