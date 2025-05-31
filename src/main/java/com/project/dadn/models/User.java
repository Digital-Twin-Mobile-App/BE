package com.project.dadn.models;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;

@Entity
@Setter
@Getter
@Builder
@Table(name = "\"users\"")
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity {
    @Id
    @GeneratedValue
    @Column(name = "user_id", columnDefinition = "UUID", updatable = false, nullable = false)
    UUID id;

    @Column(name = "email", nullable = false, unique = true, length = 50)
    String email;

    @Column(name = "password", nullable = false, length = 255)
    String password;

    @Column(name = "first_name", length = 50)
    String firstName;

    @Column(name = "last_name", length = 50)
    String lastName;

    @Column(name = "dob")
    LocalDate dob;

    @Column(name = "avatar_url")
    String avatarUrl;

    @ManyToMany
    Set<Role> roles;

    @OneToMany(mappedBy = "uploader", cascade = CascadeType.ALL)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    Collection<Image> images;
//
//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
//    private Set<FavoritePlant> favoritePlants;

}
