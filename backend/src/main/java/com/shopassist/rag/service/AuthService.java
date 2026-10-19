package com.shopassist.rag.service;

import com.shopassist.rag.dto.RegisterRequest;
import com.shopassist.rag.security.JwtService;
import com.shopassist.rag.user.AppUser;
import com.shopassist.rag.user.AppUserRepository;
import com.shopassist.rag.user.AuthResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(AppUserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

}