package com.example.demo;

import com.example.demo.config.UserConfig;
import com.example.demo.config.UserConfigList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.File;
import java.util.Arrays;

public class NoSpringTests {

    private static final String USERS_XML_NAME = "users.xml";

    public static void main(String[] args) {
        File userXmlFile = new File(args[0], USERS_XML_NAME);
        if (!userXmlFile.exists()) {
            throw new IllegalArgumentException("Check arguments: first argument is not right path. Can't find file: " + userXmlFile.getAbsolutePath());
        }
        if (true) {
            UserConfig user = new UserConfig("user", "123", Arrays.asList("USER"));
            UserConfig admin = new UserConfig("admin", "123", Arrays.asList("USER", "ADMIN"));
            UserConfigList userConfigList = new UserConfigList(Arrays.asList(user, admin));
            try {
                JAXBContext.newInstance(UserConfigList.class).createMarshaller().marshal(userConfigList, userXmlFile);
            } catch (JAXBException e) {
                e.printStackTrace();
            }
        }
        if (true) {
            try {
                UserConfigList userConfigList = (UserConfigList)JAXBContext.newInstance(UserConfigList.class).createUnmarshaller().unmarshal(userXmlFile);
                System.out.println(userConfigList);
            } catch (JAXBException e) {
                e.printStackTrace();
            }
        }

    }

}
