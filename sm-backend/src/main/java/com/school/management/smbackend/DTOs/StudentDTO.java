package com.school.management.smbackend.DTOs;

import com.school.management.smbackend.Entities.Level;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class StudentDTO {
    private Long id;
    @NotBlank
    private String username;
    @NotNull
    private Level level;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public Level getLevel() { return level; }
    public void setLevel(Level level) { this.level = level; }
}
