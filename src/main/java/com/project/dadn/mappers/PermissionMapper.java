package com.project.dadn.mappers;

import com.project.dadn.dtos.requests.PermissionRequest;
import com.project.dadn.dtos.responses.PermissionResponse;
import com.project.dadn.models.Permission;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    Permission toPermission(PermissionRequest request);
    PermissionResponse toPermissionResponse(Permission permission);
}
