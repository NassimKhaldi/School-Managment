package com.school.management.smbackend.Controllers;

import com.school.management.smbackend.DTOs.AdminLoginRequest;
import com.school.management.smbackend.DTOs.AdminRegisterRequest;
import com.school.management.smbackend.DTOs.JwtResponse;
import com.school.management.smbackend.Security.JwtUtil;
import com.school.management.smbackend.Services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Admin authentication APIs")
public class AuthController {
    private final AuthService authService;
    private final JwtUtil jwtUtil;

    public AuthController(AuthService authService, JwtUtil jwtUtil) {
        this.authService = authService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new admin")
    @ApiResponse(responseCode = "201", description = "Admin created")
    @ApiResponse(responseCode = "409", description = "Username already exists")
    @ApiResponse(responseCode = "400", description = "Invalid input")
    public ResponseEntity<String> register(@Valid @RequestBody AdminRegisterRequest req) {
        authService.register(req);
        return ResponseEntity.status(HttpStatus.CREATED).body("Admin registered successfully");
    }

    @PostMapping("/login")
    @Operation(summary = "Login and get JWT token")
    @ApiResponse(responseCode = "200", description = "Login successful")
    @ApiResponse(responseCode = "401", description = "Invalid credentials")
    @ApiResponse(responseCode = "400", description = "Invalid input")
    public JwtResponse login(@Valid @RequestBody AdminLoginRequest req) {
        authService.authenticate(req);
        String token = jwtUtil.generateToken(req.getUsername());
        return new JwtResponse(token);
    }
}
