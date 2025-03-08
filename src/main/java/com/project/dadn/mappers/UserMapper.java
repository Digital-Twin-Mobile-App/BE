package com.project.dadn.mappers;

import com.project.dadn.dtos.requests.UserCreationRequest;
import com.project.dadn.dtos.requests.UserUpdateRequest;
import com.project.dadn.dtos.responses.UserResponse;
import com.project.dadn.models.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(UserCreationRequest request);

    UserResponse toUserResponse(User user);

    @Mapping(target = "roles", ignore = true)
    void updateUser(@MappingTarget User user, UserUpdateRequest request);
}
