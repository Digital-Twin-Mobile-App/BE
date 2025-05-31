package com.project.dadn.models;

import com.project.dadn.enums.PlantStage;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "images")
public class Image{
    @Id
    @GeneratedValue
    @Column(name = "image_id", columnDefinition = "UUID", updatable = false, nullable = false)
    UUID id;

    @Column(
            name = "title",
            nullable = false
    )
    private String mediaTitle;

    @Column(
            name = "url",
            nullable = false
    )
    private String mediaUrl;

    @ManyToOne
    @JoinColumn(name = "uploader")
    private User uploader;

    @ManyToOne
    @JoinColumn(name = "plant_id")
    private Plant plant;

    @Column(name = "height_ratio")
    private Double heightRatio;

    @Column(name = "detected_species")
    private String detectedSpecies;

    @Enumerated(EnumType.STRING)
    @Column(name = "plant_stage")
    private PlantStage plantStage;

    @Column(name = "stage_confidence")
    private Double stageConfidence;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Version
    private Long version;


}

