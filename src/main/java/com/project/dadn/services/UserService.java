package com.project.dadn.services;

import com.project.dadn.dtos.requests.UserCreationRequest;
import com.project.dadn.dtos.responses.UserResponse;
import com.project.dadn.mappers.UserMapper;
import com.project.dadn.models.User;
import com.project.dadn.repositories.RoleRepository;
import com.project.dadn.repositories.UserRepository;
import com.project.dadn.exceptions.ErrorCodes;
import com.project.dadn.exceptions.AppException;
import com.project.dadn.dtos.requests.UserUpdateRequest;

import com.project.dadn.enums.RoleEnum;
import com.project.dadn.models.Role;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserService implements IUserService {
    UserRepository userRepository;
    RoleRepository roleRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;

    public UserResponse createUser(UserCreationRequest request){
        log.info("Service: Create User");
        if (userRepository.existsByUsername(request.getUsername()))
            throw new AppException(ErrorCodes.USER_EXISTED);

        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        HashSet<String> roles = new HashSet<>();
        roles.add(RoleEnum.USER.name());

        return userMapper.toUserResponse(userRepository.save(user));
    }

    public UserResponse getMyInfo(){
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();

        User user = userRepository.findByUsername(name).orElseThrow(
                () -> new AppException(ErrorCodes.USER_NOT_EXISTED));

        return userMapper.toUserResponse(user);
    }

    public UserResponse updateUser(Long userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCodes.USER_NOT_EXISTED));

        userMapper.updateUser(user, request);
        user.setPassword(passwordEncoder.encode(request.getPassWord()));

        return userMapper.toUserResponse(userRepository.save(user));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse updateAdmin(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCodes.USER_NOT_EXISTED));

        Role role = roleRepository.findById(RoleEnum.ADMIN.name())
                .orElseThrow(() -> new AppException(ErrorCodes.ROLE_NOT_FOUND));

        user.setRoles(new HashSet<>(Collections.singletonList(role)));

        return userMapper.toUserResponse(userRepository.save(user));
    }

    public void deleteUser(Long userId){
        userRepository.deleteById(userId);
    }

//    @PreAuthorize("hasAnyAuthority('APRROVE_POST')")
    public List<UserResponse> getUsers(){
        log.info("In method get Users");
        return userRepository.findAll().stream()
                .map(userMapper::toUserResponse).toList();
    }

    @PostAuthorize("returnObject.username == authentication.name")
    public UserResponse getUser(Long id){
        log.info("In method get user by Id");
        return userMapper.toUserResponse(userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCodes.USER_NOT_EXISTED)));
    }
}
