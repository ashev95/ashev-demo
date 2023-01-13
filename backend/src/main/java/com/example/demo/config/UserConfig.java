package com.example.demo.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.LinkedList;
import java.util.List;

@XmlRootElement(name = "user")
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserConfig {
    @Getter
    @XmlElement
    private String login;
    @Getter
    @XmlElement
    private String password;
    private List<String> roles = new LinkedList<>();
    @XmlElementWrapper(name = "roles")
    @XmlElement(name = "role")
    public List<String> getRoles() {
        return roles;
    }
}
