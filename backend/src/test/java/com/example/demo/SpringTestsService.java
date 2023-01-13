package com.example.demo;

import com.example.demo.model.AppUser;
import com.example.demo.model.Task;
import com.example.demo.repo.AppUserRepository;
import com.example.demo.repo.TaskRepository;
import com.example.demo.service.EntityNotFoundException;
import com.example.demo.service.TaskService;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class SpringTestsService {

	@Autowired
	private TaskService taskService;

	@Autowired
	private TaskRepository taskRepository;

	@Autowired
	private AppUserRepository appUserRepository;

	@Autowired
	private CustomJdbcUserDetailsManager customJdbcUserDetailsManager;

	private static final String USERNAME_TEST = "test";

	private static final String USERNAME_ADMIN = "admin";

	@BeforeEach
	public void prepareEach() {
		System.out.println("Clearing");
		taskRepository.deleteAll();
		Assert.assertEquals(0, taskRepository.findAll().size());
		customJdbcUserDetailsManager.deleteUser(USERNAME_TEST);
		appUserRepository.deleteAll();
		Assert.assertEquals(0, appUserRepository.findAll().size());
		customJdbcUserDetailsManager.deleteUser(USERNAME_ADMIN);
		String prefix = "{bcrypt}"; //Префикс, чтобы Spring знал, какой способ шифрования
		String salt = BCrypt.gensalt(); //Генерируем соль
		UserDetails user = User.builder()
				.username(USERNAME_ADMIN)
				.password(prefix + BCrypt.hashpw("123", salt)) //Генерируем хеш пароля
				.roles("ADMIN") //Указываем роли пользователя
				.build();
		customJdbcUserDetailsManager.createUser(user);
	}

	private String createUserAndReturnHashedPassword(String username, String password, String[] roles) {
		String prefix = "{bcrypt}"; //Префикс, чтобы Spring знал, какой способ шифрования
		String salt = BCrypt.gensalt(); //Генерируем соль
		String hashedPassword = prefix + BCrypt.hashpw(password, salt);
		UserDetails user = User.builder()
				.username(username)
				.password(hashedPassword) //Генерируем хеш пароля
				.roles(roles) //Указываем роли пользователя
				.build();
		customJdbcUserDetailsManager.createUser(user);
		return hashedPassword;
	}

	private static Stream<Arguments> crudUserTaskTestArguments() {
		return Stream.of(
				Arguments.arguments(USERNAME_TEST, "123", new String[]{"USER"}, "Задача 1")
		);
	}

	@DisplayName("Should persist task entity then update and delete")
	@ParameterizedTest
	@MethodSource("crudUserTaskTestArguments")
	@WithUserDetails(USERNAME_ADMIN)
	@Transactional(propagation = Propagation.NEVER)
	void crudUserTaskTest(String username, String password, String[] roles, String taskTitle) throws EntityNotFoundException {
		createUserAndReturnHashedPassword(username, password, roles);
		final AppUser appUser = appUserRepository.findByUsername(username);
		Task task = new Task();
		task.setTitle(taskTitle);
		task.setAppUser(appUser);
		Long id = taskService.save(task, username);
		List<Task> tasks = taskService.getByUsername(username);
		Assert.assertEquals(1, tasks.size());
		Task task1 = tasks.get(0);
		Assert.assertNotNull(task1);
		Assert.assertEquals(task1.getId(), id);
		Assert.assertEquals(task1.getTitle(), taskTitle);
		taskService.update(task1, username);
		List<Task> tasks1 = taskService.getByUsername(username);
		Assert.assertEquals(1, tasks1.size());
		Task task2 = tasks1.get(0);
		taskService.delete(task2.getId(), username);
		Assert.assertEquals(0, taskService.getAll().size());
	}

}
