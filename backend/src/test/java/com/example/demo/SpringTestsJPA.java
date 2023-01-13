package com.example.demo;

import com.example.demo.model.Task;
import com.example.demo.repo.TaskRepository;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Stream;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class SpringTestsJPA {

	@Autowired
	private TaskRepository taskRepository;

	@BeforeEach
	public void prepareEach() {
		System.out.println("Clearing");
		taskRepository.deleteAll();
		Assert.assertEquals(0, taskRepository.findAll().size());
	}

	private static Stream<Arguments> createTaskTestArguments() {
		return Stream.of(
				Arguments.arguments("Задача 1")
		);
	}

	@DisplayName("Should persist task entity without task items")
	@ParameterizedTest
	@MethodSource("createTaskTestArguments")
	@Transactional(propagation = Propagation.NEVER)
	void createTaskTest(String taskTitle) {
		Task task = new Task();
		task.setTitle(taskTitle);
		taskRepository.save(task);
		List<Task> tasks = taskRepository.findAll();
		Assert.assertEquals(1, tasks.size());
		Task task1 = tasks.get(0);
		Assert.assertNotNull(task1);
		Assert.assertEquals(task1.getTitle(), taskTitle);
	}

	private static Stream<Arguments> createAndRemoveTaskTestArguments() {
		return Stream.of(
				Arguments.arguments("Задача 1")
		);
	}

	@DisplayName("Should persist task entity without task items and remove")
	@ParameterizedTest
	@MethodSource("createAndRemoveTaskTestArguments")
	@Transactional(propagation = Propagation.NEVER)
	void createAndRemoveTaskTest(String taskTitle) {
		Task task = new Task();
		task.setTitle(taskTitle);
		taskRepository.save(task);
		List<Task> tasks = taskRepository.findAll();
		Assert.assertEquals(1, tasks.size());
		Task task1 = tasks.get(0);
		taskRepository.delete(task1);
		Assert.assertEquals(0, taskRepository.findAll().size());
	}

}
