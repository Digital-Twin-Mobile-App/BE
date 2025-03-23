package com.project.dadn.controllers;

import com.project.dadn.dtos.requests.UserCreationRequest;
import com.project.dadn.dtos.requests.UserUpdateRequest;
import com.project.dadn.dtos.responses.APIResponse;
import com.project.dadn.dtos.responses.UserResponse;
import com.project.dadn.services.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
    UserService userService;

    @PostMapping
    APIResponse<UserResponse> createUser(@RequestBody @Valid UserCreationRequest request){
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

    @PutMapping("/admin/{userId}")
    APIResponse<UserResponse> updateAdmin(@PathVariable("userId") Long userId) {
        return APIResponse.<UserResponse>builder()
                .result(userService.updateAdmin(userId))
                .build();
    }

    @GetMapping("/{userId}")
    APIResponse<UserResponse> getUser(@PathVariable("userId") Long userId){
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

    @PutMapping("/{userId}")
    APIResponse<UserResponse> updateUser(@PathVariable Long userId, @RequestBody UserUpdateRequest request){
        return APIResponse.<UserResponse>builder()
                .result(userService.updateUser(userId, request))
                .build();
    }

    @DeleteMapping("/{userId}")
    APIResponse<String> deleteUser(@PathVariable Long userId){
        userService.deleteUser(userId);
        return APIResponse.<String>builder()
                .result("User has been deleted")
                .build();
    }


}
