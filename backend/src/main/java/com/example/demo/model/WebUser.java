package com.example.demo.model;

import lombok.Getter;

import java.util.List;

@Getter
public class WebUser {
    private String login;
    private List<String> roles;
    private boolean isAuthenticated;

    public WebUser(String login, List<String> roles, boolean isAuthenticated) {
        this.login = login;
        this.roles = roles;
        this.isAuthenticated = isAuthenticated;
    }
}
