package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.web.server.ResponseStatusException;

import javax.sql.DataSource;
import java.util.List;

public class CustomJdbcUserDetailsManager extends JdbcUserDetailsManager {

    @Autowired
    LoginAttemptService loginAttemptService;

    private String usersExistsSql = "select username from users limit 1";

    private String userExistsSql = "select username from users where username = ? limit 1";

    public CustomJdbcUserDetailsManager(DataSource dataSource) {
        super(dataSource);
    }

    public boolean usersExists() {
        List<String> users = this.getJdbcTemplate().queryForList(this.usersExistsSql, String.class);
        return (users.size() != 0);
    }

    @Override
    public boolean userExists(String username) {
        List<String> users = this.getJdbcTemplate().queryForList(this.userExistsSql, String.class, new Object[]{username});
        return (users.size() != 0);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (!loginAttemptService.isAvailable(username)) {
            throw new AccessTemporarilyRestrictedException();
        }
        return super.loadUserByUsername(username);
    }
}
