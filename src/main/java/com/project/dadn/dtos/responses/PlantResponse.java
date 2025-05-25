package com.project.dadn.dtos.responses;

import com.project.dadn.enums.TreeStatus;
import com.project.dadn.enums.WateringFrequency;
import com.project.dadn.models.Category;
import com.project.dadn.models.Image;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Builder
@Data
public class PlantResponse {
    UUID id;
    String name;
    TreeStatus status;
    WateringFrequency wateringFrequency;

    List<Image> images;             // Danh sách ảnh của cây
    Set<Category> categories;       // Nhóm cây
    boolean isFavorite;
}
