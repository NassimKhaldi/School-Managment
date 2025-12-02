package com.school.management.smbackend.Services;

import com.school.management.smbackend.DTOs.AdminLoginRequest;
import com.school.management.smbackend.DTOs.AdminRegisterRequest;
import com.school.management.smbackend.Entities.Admin;
import com.school.management.smbackend.Repositories.AdminRepository;
import com.school.management.smbackend.Security.LoginAttemptService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    private final AdminRepository adminRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final LoginAttemptService loginAttemptService;

    public AuthService(AdminRepository adminRepository, BCryptPasswordEncoder passwordEncoder, LoginAttemptService loginAttemptService) {
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
        this.loginAttemptService = loginAttemptService;
    }

    @Transactional
    public Admin register(AdminRegisterRequest req) {
        if (adminRepository.existsByUsername(req.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        Admin admin = new Admin();
        admin.setUsername(req.getUsername());
        admin.setPassword(passwordEncoder.encode(req.getPassword()));
        return adminRepository.save(admin);
    }

    public Admin authenticate(AdminLoginRequest req) {
        // Check if user is blocked due to too many failed attempts
        if (loginAttemptService.isBlocked(req.getUsername())) {
            long remainingTime = loginAttemptService.getRemainingLockTime(req.getUsername());
            throw new IllegalArgumentException("Too many login attempts. Please try again in " + remainingTime + " seconds");
        }

        Admin admin = adminRepository.findByUsername(req.getUsername())
                .orElseThrow(() -> {
                    loginAttemptService.loginFailed(req.getUsername());
                    return new IllegalArgumentException("Invalid credentials");
                });
        
        if (!passwordEncoder.matches(req.getPassword(), admin.getPassword())) {
            loginAttemptService.loginFailed(req.getUsername());
            throw new IllegalArgumentException("Invalid credentials");
        }
        
        // Login successful - reset failed attempts
        loginAttemptService.loginSucceeded(req.getUsername());
        return admin;
    }
}
