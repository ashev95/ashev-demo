package com.example.demo;

import org.springframework.security.provisioning.JdbcUserDetailsManager;

import javax.sql.DataSource;
import java.util.List;

public class CustomJdbcUserDetailsManager extends JdbcUserDetailsManager {
    private String usersExistsSql = "select username from users limit 1";
    public CustomJdbcUserDetailsManager(DataSource dataSource) {
        super(dataSource);
    }
    public boolean usersExists() {
        List<String> users = this.getJdbcTemplate().queryForList(this.usersExistsSql, String.class);
        return (users.size() != 0);
    }
}
