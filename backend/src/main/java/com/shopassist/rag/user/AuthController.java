package com.shopassist.rag.user;

import com.shopassist.rag.dto.RegisterRequest;
import com.shopassist.rag.dto.UpdateRoleRequest;
import com.shopassist.rag.dto.UserResponse;
import com.shopassist.rag.security.AuthenticatedUser;
import com.shopassist.rag.security.JwtService;
import com.shopassist.rag.security.SecurityUtils;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final SecurityUtils securityUtils;
    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthController(SecurityUtils securityUtils, AppUserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          JwtService jwtService) {
        this.securityUtils = securityUtils;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }
    @GetMapping("/users/{email}")
    public UserResponse getUser(@PathVariable String email) {
        AppUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        return new UserResponse(
                user.id(),
                user.email(),
                user.tenantId(),
                user.role()
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPPORT')")
    @DeleteMapping("/users/{email}")
    @ResponseStatus(HttpStatus.OK)
    public java.util.Map<String, Object> deleteUser(@PathVariable String email) {
        AuthenticatedUser currentUser = securityUtils.currentUser();

        if (currentUser.email().equalsIgnoreCase(email)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You cannot delete your own account");
        }

        AppUser existing = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        int deleted = userRepository.deleteByEmail(email);

        return java.util.Map.of(
                "status", "ok",
                "deleted", deleted > 0,
                "email", existing.email()
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(user -> new UserResponse(
                        user.id(),
                        user.email(),
                        user.tenantId(),
                        user.role()
                ))
                .toList();
    }
    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public AuthResponse login(@Valid @RequestBody AuthRequest request) {
        AppUser user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        if (!passwordEncoder.matches(request.password(), user.passwordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        String token = jwtService.generate(user.email(), user.tenantId(), user.role());
        return new AuthResponse(token, user.email(), user.tenantId(), user.role());
    }
    @PatchMapping("/users/{email}/role")
    public AuthResponse updateRole(@PathVariable String email,
                                   @RequestBody UpdateRoleRequest request) {

        AppUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        userRepository.updateRole(email, request.role());

        String token = jwtService.generate(
                user.email(),
                user.tenantId(),
                request.role()
        );

        return new AuthResponse(
                token,
                user.email(),
                user.tenantId(),
                request.role()
        );
    }
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.OK)
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }

        String role = "CUSTOMER";

        String tenantId = "demo"; // 🔥 HERE

        userRepository.insert(
                request.email(),
                passwordEncoder.encode(request.password()),
                tenantId,
                role
        );

        String token = jwtService.generate(
                request.email(),
                tenantId,
                role
        );

        return new AuthResponse(
                token,
                request.email(),
                tenantId,
                role
        );
    }
}