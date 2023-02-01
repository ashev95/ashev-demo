package com.example.demo.controller;

import com.example.demo.model.WebUser;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/client")
public class WebUserController {

    @RequestMapping("/profile")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity profile(@AuthenticationPrincipal User user) {
        WebUser webUser = new WebUser(user.getUsername(), user.getAuthorities().stream()
                .map(grantedAuthority -> grantedAuthority.getAuthority())
                .collect(Collectors.toList()), true);
        return ResponseEntity.ok(webUser);
    }

    @RequestMapping("/language")
    @GetMapping(produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity language(HttpServletRequest request) {
        return ResponseEntity.ok(request.getLocale().toLanguageTag());
    }

}
