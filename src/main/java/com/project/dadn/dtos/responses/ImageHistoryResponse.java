package com.project.dadn.dtos.responses;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ImageHistoryResponse
{
    private UUID imageId;
    private String mediaTitle;
    private String mediaUrl;
    private UUID uploaderId;
    private String uploaderName;
    private LocalDateTime uploadDate;

}
