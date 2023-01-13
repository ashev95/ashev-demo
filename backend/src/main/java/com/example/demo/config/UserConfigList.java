package com.example.demo.config;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.LinkedList;
import java.util.List;

@XmlRootElement(name = "user_config")
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserConfigList {
    private List<UserConfig> users = new LinkedList<>();
    @XmlElementWrapper(name = "users")
    @XmlElement
    public List<UserConfig> getUsers() {
        return users;
    }
}
