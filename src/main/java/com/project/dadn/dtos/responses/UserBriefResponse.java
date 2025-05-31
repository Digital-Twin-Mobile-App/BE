package com.project.dadn.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserBriefResponse {
    private UUID id;
    private String email;
    private String fullName;
    private String avatarUrl;
}