package com.project.dadn.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.PrePersist;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public class BaseEntity {
    @Column(name = "created_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private ZonedDateTime createdAt;

    @Column(name = "updated_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private ZonedDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
        updatedAt = ZonedDateTime.now(ZoneId.of("Asia/Ho_Chi_Minh"));
    }

}
