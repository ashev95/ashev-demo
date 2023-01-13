package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "task")
@NoArgsConstructor
@Getter
@Setter
public class Task implements Serializable {

    @Id
    @GeneratedValue
    private Long id;
    private String title;
    private String description;
    private boolean completed;
    @JsonIgnore
    @ManyToOne(optional = false)
    private AppUser appUser;

    public Task(String title, String description, boolean completed, AppUser appUser) {
        this.title = title;
        this.description = description;
        this.completed = completed;
        this.appUser = appUser;
    }
}
