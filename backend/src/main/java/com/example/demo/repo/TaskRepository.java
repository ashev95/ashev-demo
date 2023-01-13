package com.example.demo.repo;

import com.example.demo.model.Task;
import com.example.demo.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query(value = "from Task t where t.appUser.username.username= :username")
    List<Task> findByUsername(@Param("username") String username);

    @Query(value = "from Task t where t.id = :id and t.appUser.username.username= :username")
    Task findByIdAndUsername(@Param("id") Long id, @Param("username") String username);


}