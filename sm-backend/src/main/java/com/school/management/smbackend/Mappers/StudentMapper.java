package com.school.management.smbackend.Mappers;

import com.school.management.smbackend.DTOs.StudentDTO;
import com.school.management.smbackend.Entities.Student;
import org.springframework.stereotype.Component;

@Component
public class StudentMapper {
    public StudentDTO toDTO(Student s) {
        if (s == null) return null;
        StudentDTO dto = new StudentDTO();
        dto.setId(s.getId());
        dto.setUsername(s.getUsername());
        dto.setLevel(s.getLevel());
        return dto;
    }

    public Student fromDTO(StudentDTO dto) {
        if (dto == null) return null;
        Student s = new Student();
        s.setId(dto.getId());
        s.setUsername(dto.getUsername());
        s.setLevel(dto.getLevel());
        return s;
    }
}
