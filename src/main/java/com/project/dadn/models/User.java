package com.project.dadn.models;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Data
@Builder
@Table(name = "users")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    Long id;

    @Column(name = "username", nullable = false, unique = true, length = 50)
    String username;

    @Column(name = "password", nullable = false, length = 255)
    String password;

    @Column(name = "first_name", length = 50)
    String firstName;

    @Column(name = "last_name", length = 50)
    String lastName;

    @Column(name = "dob")
    LocalDate dob;

    @ManyToMany
    Set<Role> roles;
}
