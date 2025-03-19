package com.project.dadn.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Image {
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

    @ManyToOne(
            n
    )
    private String mediaFileId;
}

