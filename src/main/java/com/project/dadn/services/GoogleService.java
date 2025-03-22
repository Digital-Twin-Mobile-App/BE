package com.project.dadn.services;

import com.project.dadn.dtos.requests.UserCreationRequest;
import com.project.dadn.dtos.responses.AuthenticationResponse;
import com.project.dadn.exceptions.AppException;
import com.project.dadn.exceptions.ErrorCodes;
import com.project.dadn.models.User;
import com.project.dadn.repositories.UserRepository;
import com.project.dadn.utlls.TokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class GoogleService {

    private final UserRepository userRepository;
    private final TokenUtil tokenUtil;
    private final UserService userService;

    public AuthenticationResponse authenticateWithGoogle(OAuth2AuthenticationToken authentication) {
        OAuth2User oauth2User = authentication.getPrincipal();

        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");

        User user = userRepository.findByEmail(email).orElseGet(() -> {
            // Tách tên thành firstName và lastName nếu có
            String firstName = name != null ? name.split(" ")[0] : "Google";
            String lastName = name != null && name.contains(" ") ? name.substring(name.indexOf(" ") + 1) : "User";

            // Tạo yêu cầu tạo mới người dùng
            UserCreationRequest req = UserCreationRequest.builder()
                    .email(email)
                    .password("google-oauth")
                    .firstName(firstName)
                    .lastName(lastName)
                    .dob(LocalDate.now()) // Giả định ngày sinh là ngày hiện tại
                    .build();

            // Gọi hàm tạo người dùng
            userService.createUser(req);

            // Lấy lại người dùng từ cơ sở dữ liệu sau khi tạo
            return userRepository.findByEmail(email).orElseThrow(() ->
                    new AppException(ErrorCodes.USER_CREATION_FAILED)
            );
        });


        assert user != null;
        String token = tokenUtil.generateToken(user);

        return AuthenticationResponse.buildAuthenticationResponse(token, true);
    }


}
