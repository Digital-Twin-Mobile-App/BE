package com.project.dadn.mappers;

import com.project.dadn.dtos.responses.PlantResponse;
import com.project.dadn.models.Plant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
public interface PlantMapper {
    @Mapping(target = "isFavorite", ignore = true) // xử lý riêng nếu cần
    PlantResponse toResponse(Plant plant);
}
