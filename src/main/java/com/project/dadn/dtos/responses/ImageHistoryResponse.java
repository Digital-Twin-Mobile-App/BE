package com.project.dadn.dtos.responses;

import com.project.dadn.enums.PlantStage;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ImageHistoryResponse
{
    private UUID imageId;
    private UUID plantId;
    private String mediaTitle;
    private String mediaUrl;
    private UUID uploaderId;
    private String uploaderName;
    private LocalDateTime uploadDate;
    private PlantStage plantStage;
    private Double stageConfidence;
    private String detectedSpecies;

}
