package com.example.demo;

import com.example.demo.config.UserConfig;
import com.example.demo.config.UserConfigList;
import com.example.demo.model.Task;
import com.example.demo.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBContext;
import java.io.InputStream;

@Component
@Profile("tst")
@Slf4j
public class DebugConfig {

	@Autowired
	private TaskService taskService;

	@Autowired
	private CustomJdbcUserDetailsManager customJdbcUserDetailsManager;

	@Value("classpath:config/users.xml")
	Resource usersXml;

	@PostConstruct
	public void generateTestData() {
		final Task task = new Task();
		task.setTitle("МФЦ");
		task.setDescription("Отнести документы в МФЦ");
		task.setCompleted(false);
		taskService.save(task, "user");
		log.info("Task created for debugging");
	}
}