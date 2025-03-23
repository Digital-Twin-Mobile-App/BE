package com.project.dadn.utlls;


import com.nimbusds.jwt.SignedJWT;
import com.project.dadn.exceptions.AppException;
import com.project.dadn.exceptions.ErrorCodes;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.text.ParseException;

@Component
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JwtUtil {

    public String getTokenVersion(String token) throws ParseException {
        SignedJWT signedJWT = SignedJWT.parse(token);

        Object tokenVersionObj = signedJWT.getJWTClaimsSet().getClaim("token_version");

        if (tokenVersionObj instanceof String str) {
            return str;
        } else if (tokenVersionObj instanceof Number number) {
            return String.valueOf(number);
        }

        throw new AppException(ErrorCodes.INVALID_TOKEN_VERSION);
    }

    public String getEmailToken(String token) throws ParseException {
        SignedJWT signedJWT = SignedJWT.parse(token);
        return signedJWT.getJWTClaimsSet().getSubject();

    }
}
