package com.school.management.smbackend.Services;

import com.school.management.smbackend.DTOs.StudentDTO;
import com.school.management.smbackend.Entities.Level;
import com.school.management.smbackend.Entities.Student;
import com.school.management.smbackend.Repositories.StudentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import com.school.management.smbackend.Mappers.StudentMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StudentService {
    private final StudentRepository studentRepository;
    private final StudentMapper mapper;

    public StudentService(StudentRepository studentRepository, StudentMapper mapper) {
        this.studentRepository = studentRepository;
        this.mapper = mapper;
    }

    public Page<StudentDTO> list(Integer page, Integer size, String search, Level level) {
        Pageable pageable = PageRequest.of(page == null ? 0 : page, size == null ? 10 : size);
        Page<Student> pageData;
        if (search != null && !search.isBlank()) {
            pageData = studentRepository.findByUsernameContainingIgnoreCase(search, pageable);
        } else if (level != null) {
            pageData = studentRepository.findByLevel(level, pageable);
        } else {
            pageData = studentRepository.findAll(pageable);
        }
        return pageData.map(mapper::toDTO);
    }

    public StudentDTO get(Long id) {
        return studentRepository.findById(id).map(mapper::toDTO)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));
    }

    @Transactional
    public StudentDTO create(StudentDTO dto) {
        if (studentRepository.existsByUsername(dto.getUsername())) {
            throw new IllegalArgumentException("Student username exists");
        }
        Student s = new Student();
        s.setUsername(dto.getUsername());
        s.setLevel(dto.getLevel());
        return mapper.toDTO(studentRepository.save(s));
    }

    @Transactional
    public StudentDTO update(Long id, StudentDTO dto) {
        Student s = studentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));
        s.setUsername(dto.getUsername());
        s.setLevel(dto.getLevel());
        return mapper.toDTO(studentRepository.save(s));
    }

    @Transactional
    public void delete(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new IllegalArgumentException("Student not found");
        }
        studentRepository.deleteById(id);
    }

    public List<StudentDTO> search(String username) {
        return studentRepository.findByUsernameContainingIgnoreCase(username).stream().map(mapper::toDTO).toList();
    }
}
