package com.project.dadn.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.dadn.enums.PlantStage;
import com.project.dadn.enums.TreeStatus;
import com.project.dadn.enums.WateringFrequency;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Setter
@Getter
public class Plant extends BaseEntity{
    @Id
    @GeneratedValue
    UUID id;

    @Column(nullable = false)
    String name;

    @Enumerated(EnumType.STRING)
    TreeStatus status; // khỏe, có vấn đề,...

    @Enumerated(EnumType.STRING)
    PlantStage currentStage; // Thêm trường này

    @Column(nullable = true)
    Double lastStageConfidence = 0.0; // Độ tin cậy của stage prediction

    @ManyToOne
    @JoinColumn(name = "user_id")
    User owner;

    @ManyToMany
    @JoinTable(
            name = "plant_category",
            joinColumns = @JoinColumn(name = "plant_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    Set<Category> categories;

    @Enumerated(EnumType.STRING)
    @Column(name = "watering_frequency", nullable = false)
    WateringFrequency wateringFrequency;

    private String plantCoverUrl;

    @Version
    private Long version;

}

