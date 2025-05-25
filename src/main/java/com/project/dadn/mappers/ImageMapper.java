package com.project.dadn.mappers;

import com.project.dadn.dtos.responses.ImageHistoryResponse;
import com.project.dadn.models.Image;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring")
public interface ImageMapper {
    @Mapping(target = "imageId", source = "id")
    @Mapping(target = "uploaderId", source = "uploader.id")
    @Mapping(target = "uploaderName", expression = "java(image.getUploader().getFirstName() + \" \" + image.getUploader().getLastName())")
    @Mapping(target = "uploadDate", source = "createdAt")
    ImageHistoryResponse toImageHistoryResponse(Image image);

    default Page<ImageHistoryResponse> toImageHistoryResponsePage(Page<Image> imagePage) {
        return imagePage.map(this::toImageHistoryResponse);
    }
}