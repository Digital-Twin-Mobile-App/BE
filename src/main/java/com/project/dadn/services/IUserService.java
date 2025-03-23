package com.project.dadn.services;


import com.project.dadn.dtos.requests.UserCreationRequest;
import com.project.dadn.dtos.requests.UserUpdateRequest;
import com.project.dadn.dtos.responses.UserResponse;

import java.util.List;

public interface IUserService {
    public UserResponse createUser(UserCreationRequest request);
    public UserResponse updateUser(Long userId, UserUpdateRequest request);
    public void deleteUser(Long userId);
    public List<UserResponse> getUsers();
    public UserResponse getUser(Long id);
    public UserResponse updateAdmin(Long userId);
}
