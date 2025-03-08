package com.project.dadn.mappers;

import com.project.dadn.dtos.requests.RoleRequest;
import com.project.dadn.dtos.responses.RoleResponse;
import com.project.dadn.models.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @Mapping(target = "permissions", ignore = true)
    Role toRole(RoleRequest request);

    RoleResponse toRoleResponse(Role role);
}
