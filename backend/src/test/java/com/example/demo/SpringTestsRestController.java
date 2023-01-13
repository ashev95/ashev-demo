package com.example.demo;

import com.example.demo.config.UserConfig;
import com.example.demo.model.AppUser;
import com.example.demo.model.Task;
import com.example.demo.repo.AppUserRepository;
import com.example.demo.repo.TaskRepository;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.stream.Stream;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SpringTestsRestController {

	@Autowired
	private TaskRepository taskRepository;

	@Autowired
	private AppUserRepository appUserRepository;

	@Autowired
	private MockMvc mvc;

	@Autowired
	private CustomJdbcUserDetailsManager customJdbcUserDetailsManager;

	private static String id;

	private static final String USERNAME_TEST = "test";

	@BeforeEach
	public void prepareEach() {
		System.out.println("Clearing");
		taskRepository.deleteAll();
		Assert.assertEquals(0, taskRepository.findAll().size());
		customJdbcUserDetailsManager.deleteUser(USERNAME_TEST);
		appUserRepository.deleteAll();
		Assert.assertEquals(0, appUserRepository.findAll().size());
	}

	private static Stream<Arguments> createUserTestArguments() {
		return Stream.of(
				Arguments.arguments(USERNAME_TEST, "123", new String[]{"USER"})
		);
	}

	@DisplayName("Create user")
	@ParameterizedTest
	@MethodSource("createUserTestArguments")
	void createNewUserTest(String username, String password, String[] roles) throws Exception {
		UserConfig userConfig = new UserConfig(username, password, Arrays.asList(roles));
		final int status = mvc.perform(post("/user/new")
						.with(csrf())
						.contentType(MediaType.APPLICATION_JSON)
						.content(asJsonString(userConfig)))
				.andExpect(status().isCreated())
				.andReturn().getResponse().getStatus();
		Assert.assertEquals(HttpStatus.CREATED.value(), status);
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

	private static Stream<Arguments> crudTaskTestArguments() {
		return Stream.of(
				Arguments.arguments(USERNAME_TEST, "123", new String[]{"USER"}, "Задача 1", "Сделать что-то", Boolean.FALSE)
		);
	}

	@DisplayName("Create task")
	@ParameterizedTest
	@MethodSource("crudTaskTestArguments")
	@WithMockUser(roles = "USER")
	void crudTaskTest(String username, String password, String[] roles, String taskTitle, String taskDescription, boolean taskExecuted) throws Exception {
		createUserAndReturnHashedPassword(username, password, roles);
		final AppUser appUser = appUserRepository.findByUsername(username);
		Task task = new Task(taskTitle, taskDescription, taskExecuted, appUser);
		id = mvc.perform(post("/task")
						.with(csrf())
						.contentType(MediaType.APPLICATION_JSON)
						.content(asJsonString(task))
						.accept(MediaType.TEXT_PLAIN_VALUE))
				.andExpect(status().isCreated())
				.andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN_VALUE))
				.andReturn().getResponse().getContentAsString();

		mvc.perform(get("/task/" + id)
						.with(csrf())
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.id").value(id))
				.andExpect(jsonPath("$.title").value(taskTitle))
				.andExpect(jsonPath("$.description").value(taskDescription))
				.andExpect(jsonPath("$.completed").value(taskExecuted));

		task.setId(Long.parseLong(id));

		mvc.perform(put("/task")
						.with(csrf())
						.contentType(MediaType.APPLICATION_JSON)
						.content(asJsonString(task))
						.accept(MediaType.TEXT_PLAIN_VALUE))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN_VALUE))
				.andExpect(content().string(id));

		mvc.perform(get("/task/" + id)
						.with(csrf())
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.id").value(id))
				.andExpect(jsonPath("$.title").value(taskTitle))
				.andExpect(jsonPath("$.description").value(taskDescription))
				.andExpect(jsonPath("$.completed").value(taskExecuted));

		mvc.perform(delete("/task/" + id)
				.with(csrf())
				.contentType(MediaType.TEXT_PLAIN_VALUE))
				.andExpect(status().isNoContent());

		mvc.perform(get("/task/" + id)
						.with(csrf())
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNoContent());

	}

	public static String asJsonString(final Object obj) {
		try {
			return new ObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
