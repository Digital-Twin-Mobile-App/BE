package com.project.dadn.models;

import jakarta.persistence.*;

import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue
    @Column(name = "category_id", columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    // Mối quan hệ nhiều-nhiều với Tree
    @ManyToMany(mappedBy = "categories")
    private Set<Plant> plants;

    // Optional: category có thể do user tạo ra
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User owner;
}

