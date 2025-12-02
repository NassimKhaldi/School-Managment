package com.school.management.smbackend.Services;

import com.school.management.smbackend.DTOs.AdminLoginRequest;
import com.school.management.smbackend.DTOs.AdminRegisterRequest;
import com.school.management.smbackend.Entities.Admin;
import com.school.management.smbackend.Repositories.AdminRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    private final AdminRepository adminRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthService(AdminRepository adminRepository, BCryptPasswordEncoder passwordEncoder) {
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
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
        Admin admin = adminRepository.findByUsername(req.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));
        if (!passwordEncoder.matches(req.getPassword(), admin.getPassword())) {
            throw new IllegalArgumentException("Invalid credentials");
        }
        return admin;
    }
}
