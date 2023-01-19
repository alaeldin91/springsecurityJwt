package com.alaeldin.security.auth.service;

import com.alaeldin.security.auth.request.AuthenticateRequest;
import com.alaeldin.security.auth.request.RegisterRequest;
import com.alaeldin.security.auth.response.AuthenticationResponse;
import com.alaeldin.security.config.JwtService;
import com.alaeldin.security.user.entity.Role;
import com.alaeldin.security.user.entity.User;
import com.alaeldin.security.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest registerRequest) {
        var user = User.builder()
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .roleName(Role.USER).build();
        userRepository.save(user);
        var token = jwtService.generateToken(user);
        return AuthenticationResponse.builder().token(token).build();

    }

    public AuthenticationResponse authenticate(AuthenticateRequest authenticateRequest) {

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticateRequest
                .getEmail(), authenticateRequest.getPassword()));
        var user = userRepository.findByEmail(authenticateRequest.getEmail())
                .orElseThrow();
        var token = jwtService.generateToken(user);
        return AuthenticationResponse.builder().token(token).build();
    }
}
