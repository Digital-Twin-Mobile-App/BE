package com.project.dadn.controllers;

import com.project.dadn.dtos.requests.UserCreationRequest;
import com.project.dadn.dtos.requests.UserUpdateRequest;
import com.project.dadn.dtos.responses.APIResponse;
import com.project.dadn.dtos.responses.UserResponse;
import com.project.dadn.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
    UserService userService;

    @PostMapping
    APIResponse<UserResponse> createUser(
            @RequestBody @Valid UserCreationRequest request){
        return APIResponse.<UserResponse>builder()
                .result(userService.createUser(request))
                .build();
    }

    @GetMapping
    APIResponse<List<UserResponse>> getUsers(){
        return APIResponse.<List<UserResponse>>builder()
                .result(userService.getUsers())
                .build();
    }

    @PutMapping("/admin")
    APIResponse<UserResponse> updateAdmin(
            @RequestParam("userId") UUID userId) {
        return APIResponse.<UserResponse>builder()
                .result(userService.updateAdmin(userId))
                .build();
    }

    @GetMapping("/name")
    APIResponse<UserResponse> getUser(
            @RequestParam("userId") UUID userId){
        return APIResponse.<UserResponse>builder()
                .result(userService.getUser(userId))
                .build();
    }

    @GetMapping("/myInfo")
    APIResponse<UserResponse> getMyInfo(){
        return APIResponse.<UserResponse>builder()
                .result(userService.getMyInfo())
                .build();
    }

    @PutMapping
    APIResponse<UserResponse> updateUser(
            @RequestBody UserUpdateRequest request){
        return APIResponse.<UserResponse>builder()
                .result(userService.updateUser(request))
                .build();
    }

    @PatchMapping(path = "/update-user")
    public APIResponse<UserResponse> updateUserWithAvatar(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String dob,
            @RequestPart(value = "avatar", required = false) MultipartFile avatarFile,
            HttpServletRequest request) throws ParseException {

        UserUpdateRequest userRequest = UserUpdateRequest.builder()
                .firstName(firstName)
                .lastName(lastName)
                .dob(dob != null ? LocalDate.parse(dob) : null)
                .build();

        return APIResponse.<UserResponse>builder()
                .result(userService.updateUserWithAvatar(userRequest, avatarFile, request))
                .build();
    }


    @DeleteMapping
    APIResponse<String> deleteUser(
            @RequestParam UUID userId){
        userService.deleteUser(userId);
        return APIResponse.<String>builder()
                .result("User has been deleted")
                .build();
    }
}
