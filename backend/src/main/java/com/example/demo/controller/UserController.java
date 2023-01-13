package com.example.demo.controller;

import com.example.demo.CustomJdbcUserDetailsManager;
import com.example.demo.config.UserConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private CustomJdbcUserDetailsManager customJdbcUserDetailsManager;

    @RequestMapping("/new")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity create(@RequestBody UserConfig userConfig) {
        boolean userExists = true;
        try {
            customJdbcUserDetailsManager.loadUserByUsername(userConfig.getLogin());
        } catch (UsernameNotFoundException e) {
            userExists = false;
        }
        if (userExists) {
            return ResponseEntity.badRequest().build();
        }
        String prefix = "{bcrypt}"; //Префикс, чтобы Spring знал, какой способ шифрования
        String salt = BCrypt.gensalt(); //Генерируем соль
        UserDetails user = User.builder()
                .username(userConfig.getLogin())
                .password(prefix + BCrypt.hashpw(userConfig.getPassword(), salt)) //Генерируем хеш пароля
                .roles(new String[]{"USER"}) //Указываем роли пользователя
                .build();
        customJdbcUserDetailsManager.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
