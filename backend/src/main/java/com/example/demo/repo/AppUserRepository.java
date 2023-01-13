package com.example.demo.repo;

import com.example.demo.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    @Query(value = "from AppUser u where u.username.username= :username")
    AppUser findByUsername(@Param("username") String username);

}