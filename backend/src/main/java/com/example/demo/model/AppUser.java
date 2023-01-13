package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AppUser implements Serializable {

    @EmbeddedId
    private UsernamePK username;
    @Column(length = 500, nullable = false)
    private String password;
    @Column(nullable = false)
    private boolean enabled;
    @OneToMany(mappedBy = "appUser", cascade = CascadeType.REMOVE)
    private Set<Task> tasks;

}

