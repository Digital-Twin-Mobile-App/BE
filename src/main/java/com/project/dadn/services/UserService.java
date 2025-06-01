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
import com.project.dadn.utlls.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserService {
    UserRepository userRepository;
    RoleRepository roleRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;
    JwtUtil jwtUtil;
    private final UploadFileService uploadFileService;

    public UserResponse createUser(UserCreationRequest request){
        log.info("Service: Create User");
        if (userRepository.existsByEmail(request.getEmail()))
            throw new AppException(ErrorCodes.USER_EXISTED);

        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        Role role = roleRepository.findById(RoleEnum.USER.name())
                .orElseThrow(() -> new AppException(ErrorCodes.ROLE_NOT_FOUND));

        user.setRoles(Collections.singleton(role));

        return userMapper.toUserResponse(userRepository.save(user));
    }

    public UserResponse getMyInfo(){
        var context = SecurityContextHolder.getContext();
        String email = context.getAuthentication().getName();

        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new AppException(ErrorCodes.USER_NOT_EXISTED));

        return userMapper.toUserResponse(user);
    }

    public UserResponse updateUser(UserUpdateRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCodes.USER_NOT_EXISTED));

        userMapper.updateUser(user, request);

        return userMapper.toUserResponse(userRepository.save(user));
    }

    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse updateAdmin(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCodes.USER_NOT_EXISTED));

        Role role = roleRepository.findById(RoleEnum.ADMIN.name())
                .orElseThrow(() -> new AppException(ErrorCodes.ROLE_NOT_FOUND));

        user.setRoles(new HashSet<>(Collections.singletonList(role)));

        return userMapper.toUserResponse(userRepository.save(user));
    }

    public void deleteUser(UUID userId){
        userRepository.deleteById(userId);
    }

//    @PreAuthorize("hasAnyAuthority('APRROVE_POST')")
    public List<UserResponse> getUsers(){
        log.info("In method get Users");
        return userRepository.findAll().stream()
                .map(userMapper::toUserResponse).toList();
    }

    @PostAuthorize("returnObject.username == authentication.name")
    public UserResponse getUser(UUID id){
        log.info("In method get user by Id");
        return userMapper.toUserResponse(userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCodes.USER_NOT_EXISTED)));
    }

    @Transactional
    public UserResponse updateUserWithAvatar(UserUpdateRequest userRequest, MultipartFile avatarFile, HttpServletRequest request) throws ParseException {
        String userToken = jwtUtil.getUserToken(request);
        String userEmail = jwtUtil.getEmailToken(userToken);
        User user = userRepository.findByEmail(userEmail).orElseThrow(
                () -> new AppException(ErrorCodes.USER_NOT_EXISTED)
        );

        if (userRequest.getFirstName() != null)
            user.setFirstName(userRequest.getFirstName());

        if (userRequest.getLastName() != null)
            user.setLastName(userRequest.getLastName());

        if (userRequest.getDob() != null)
            user.setDob(userRequest.getDob());

        User res = userRepository.save(user);

        UserResponse response = userMapper.toUserResponse(res); //

        if (avatarFile != null && !avatarFile.isEmpty()) {
            try {
                String fileName = UUID.randomUUID() + "_" + avatarFile.getOriginalFilename();
                String baseDir = System.getProperty("user.dir") + "/uploads/";
                java.io.File dir = new java.io.File(baseDir);
                if (!dir.exists()) dir.mkdirs();

                java.io.File tempFile = new java.io.File(baseDir + fileName);
                avatarFile.transferTo(tempFile);

//                String imageUrl = uploadFileService.uploadImageToDriveAndReturnUrl(tempFile);
                uploadFileService.uploadAvatarAsync(res, tempFile);


            } catch (IOException e) {
                throw new RuntimeException("Không thể upload avatar lên Google Drive", e);
            } catch (Exception e) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                throw new RuntimeException(e);
            }
        }


        return response;
    }

}
