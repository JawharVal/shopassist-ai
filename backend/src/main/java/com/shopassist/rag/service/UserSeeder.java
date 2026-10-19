package com.shopassist.rag.service;

import com.shopassist.rag.user.AppUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserSeeder implements CommandLineRunner {

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserSeeder(AppUserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            userRepository.insert("admin@demo.com", passwordEncoder.encode("admin123"), "demo", "ADMIN");
            userRepository.insert("support@demo.com", passwordEncoder.encode("support123"), "demo", "SUPPORT");
            userRepository.insert("customer@demo.com", passwordEncoder.encode("customer123"), "demo", "CUSTOMER");
        }
    }
}
