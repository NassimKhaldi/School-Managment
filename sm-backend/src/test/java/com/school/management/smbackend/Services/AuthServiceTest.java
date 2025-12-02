package com.school.management.smbackend.Services;

import com.school.management.smbackend.DTOs.AdminLoginRequest;
import com.school.management.smbackend.DTOs.AdminRegisterRequest;
import com.school.management.smbackend.Entities.Admin;
import com.school.management.smbackend.Repositories.AdminRepository;
import com.school.management.smbackend.Security.LoginAttemptService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private LoginAttemptService loginAttemptService;

    @InjectMocks
    private AuthService authService;

    private Admin testAdmin;
    private AdminRegisterRequest registerRequest;
    private AdminLoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        testAdmin = new Admin();
        testAdmin.setId(1L);
        testAdmin.setUsername("testadmin");
        testAdmin.setPassword("hashedPassword123");

        registerRequest = new AdminRegisterRequest();
        registerRequest.setUsername("newadmin");
        registerRequest.setPassword("password123");

        loginRequest = new AdminLoginRequest();
        loginRequest.setUsername("testadmin");
        loginRequest.setPassword("password123");
    }

    @Test
    void testRegister_Success() {
        // Arrange
        when(adminRepository.existsByUsername(registerRequest.getUsername())).thenReturn(false);
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("hashedPassword");
        when(adminRepository.save(any(Admin.class))).thenReturn(testAdmin);

        // Act
        Admin result = authService.register(registerRequest);

        // Assert
        assertNotNull(result);
        verify(adminRepository).existsByUsername(registerRequest.getUsername());
        verify(passwordEncoder).encode(registerRequest.getPassword());
        verify(adminRepository).save(any(Admin.class));
    }

    @Test
    void testRegister_UsernameAlreadyExists() {
        // Arrange
        when(adminRepository.existsByUsername(registerRequest.getUsername())).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> authService.register(registerRequest)
        );

        assertEquals("Username already exists", exception.getMessage());
        verify(adminRepository).existsByUsername(registerRequest.getUsername());
        verify(adminRepository, never()).save(any(Admin.class));
    }

    @Test
    void testAuthenticate_Success() {
        // Arrange
        when(loginAttemptService.isBlocked(loginRequest.getUsername())).thenReturn(false);
        when(adminRepository.findByUsername(loginRequest.getUsername())).thenReturn(Optional.of(testAdmin));
        when(passwordEncoder.matches(loginRequest.getPassword(), testAdmin.getPassword())).thenReturn(true);

        // Act
        Admin result = authService.authenticate(loginRequest);

        // Assert
        assertNotNull(result);
        assertEquals(testAdmin.getUsername(), result.getUsername());
        verify(loginAttemptService).isBlocked(loginRequest.getUsername());
        verify(adminRepository).findByUsername(loginRequest.getUsername());
        verify(passwordEncoder).matches(loginRequest.getPassword(), testAdmin.getPassword());
        verify(loginAttemptService).loginSucceeded(loginRequest.getUsername());
    }

    @Test
    void testAuthenticate_InvalidUsername() {
        // Arrange
        when(loginAttemptService.isBlocked(loginRequest.getUsername())).thenReturn(false);
        when(adminRepository.findByUsername(loginRequest.getUsername())).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> authService.authenticate(loginRequest)
        );

        assertEquals("Invalid credentials", exception.getMessage());
        verify(loginAttemptService).loginFailed(loginRequest.getUsername());
        verify(loginAttemptService, never()).loginSucceeded(anyString());
    }

    @Test
    void testAuthenticate_InvalidPassword() {
        // Arrange
        when(loginAttemptService.isBlocked(loginRequest.getUsername())).thenReturn(false);
        when(adminRepository.findByUsername(loginRequest.getUsername())).thenReturn(Optional.of(testAdmin));
        when(passwordEncoder.matches(loginRequest.getPassword(), testAdmin.getPassword())).thenReturn(false);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> authService.authenticate(loginRequest)
        );

        assertEquals("Invalid credentials", exception.getMessage());
        verify(loginAttemptService).loginFailed(loginRequest.getUsername());
        verify(loginAttemptService, never()).loginSucceeded(anyString());
    }

    @Test
    void testAuthenticate_AccountBlocked() {
        // Arrange
        when(loginAttemptService.isBlocked(loginRequest.getUsername())).thenReturn(true);
        when(loginAttemptService.getRemainingLockTime(loginRequest.getUsername())).thenReturn(45L);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> authService.authenticate(loginRequest)
        );

        assertTrue(exception.getMessage().contains("Too many login attempts"));
        verify(loginAttemptService).isBlocked(loginRequest.getUsername());
        verify(adminRepository, never()).findByUsername(anyString());
    }

    @Test
    void testAuthenticate_ClearsAttemptsOnSuccess() {
        // Arrange
        when(loginAttemptService.isBlocked(loginRequest.getUsername())).thenReturn(false);
        when(adminRepository.findByUsername(loginRequest.getUsername())).thenReturn(Optional.of(testAdmin));
        when(passwordEncoder.matches(loginRequest.getPassword(), testAdmin.getPassword())).thenReturn(true);

        // Act
        authService.authenticate(loginRequest);

        // Assert
        verify(loginAttemptService).loginSucceeded(loginRequest.getUsername());
    }
}
