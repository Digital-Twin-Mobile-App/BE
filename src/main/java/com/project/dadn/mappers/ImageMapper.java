package com.project.dadn.mappers;

import com.project.dadn.dtos.responses.ImageHistoryResponse;
import com.project.dadn.models.Image;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ImageMapper {
    @Mapping(target = "imageId", source = "id")
    @Mapping(target = "plantId", source = "plant.id")
    @Mapping(target = "uploaderId", source = "uploader.id")
    @Mapping(target = "uploaderName", expression = "java(image.getUploader().getFirstName() + \" \" + image.getUploader().getLastName())")
    @Mapping(target = "uploadDate", source = "createdAt")
    @Mapping(target = "plantStage", source = "plantStage")
    @Mapping(target = "stageConfidence", source = "stageConfidence")
    ImageHistoryResponse toImageHistoryResponse(Image image);

    default Page<ImageHistoryResponse> toImageHistoryResponsePage(Page<Image> imagePage) {
        return imagePage.map(this::toImageHistoryResponse);
    }

    default List<ImageHistoryResponse> toImageHistoryResponseList(List<Image> imageList) {
        return imageList.stream().map(this::toImageHistoryResponse).toList();
    }
}