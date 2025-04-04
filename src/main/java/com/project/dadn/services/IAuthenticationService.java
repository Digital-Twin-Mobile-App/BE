package com.project.dadn.services;

import com.nimbusds.jose.JOSEException;
import com.project.dadn.dtos.requests.AuthenticationRequest;
import com.project.dadn.dtos.requests.IntrospectRequest;
import com.project.dadn.dtos.responses.AuthenticationResponse;
import com.project.dadn.dtos.responses.IntrospectResponse;

import java.text.ParseException;

public interface IAuthenticationService {
    public IntrospectResponse introspect(IntrospectRequest request)
            throws ParseException, JOSEException;
    public AuthenticationResponse authenticate(AuthenticationRequest request);
}
