package com.example.demo.service;

import com.example.demo.model.Task;
import com.example.demo.repo.AppUserRepository;
import com.example.demo.repo.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private AppUserRepository appUserRepository;

    public Long save(Task task, String username) {
        final Task newTask = new Task();
        newTask.setTitle(task.getTitle());
        newTask.setDescription(task.getDescription());
        newTask.setCompleted(task.isCompleted());
        newTask.setAppUser(appUserRepository.findByUsername(username));
        return taskRepository.save(newTask).getId();
    }

    public Long update(Task task, String username) throws EntityNotFoundException {
        final Task task1 = taskRepository.findByIdAndUsername(task.getId(), username);
        if (task1 == null) {
            throw new EntityNotFoundException("Task with ID [" + task.getId() + "] and username [" + username + "] not found");
        }
        task1.setTitle(task.getTitle());
        task1.setDescription(task.getDescription());
        task1.setCompleted(task.isCompleted());
        return taskRepository.save(task1).getId();
    }

    public Task get(Long id) {
        return taskRepository.findById(id).orElse(null);
    }

    public Task get(Long id, String username) {
        return taskRepository.findByIdAndUsername(id, username);
    }

    public List<Task> getAll() {
        return taskRepository.findAll();
    }

    public List<Task> getByUsername(String username) {
        return taskRepository.findByUsername(username);
    }

    public void delete(long id, String username) throws EntityNotFoundException {
        final Task task = taskRepository.findByIdAndUsername(id, username);
        if (task == null) {
            throw new EntityNotFoundException("Task with ID [" + task.getId() + "] and username [" + username + "] not found");
        }
        taskRepository.deleteById(id);
    }

}
