package com.school.management.smbackend.Services;

import com.school.management.smbackend.DTOs.StudentDTO;
import com.school.management.smbackend.Entities.Level;
import com.school.management.smbackend.Entities.Student;
import com.school.management.smbackend.Mappers.StudentMapper;
import com.school.management.smbackend.Repositories.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private StudentMapper mapper;

    @InjectMocks
    private StudentService studentService;

    private Student testStudent;
    private StudentDTO testStudentDTO;

    @BeforeEach
    void setUp() {
        testStudent = new Student();
        testStudent.setId(1L);
        testStudent.setUsername("johndoe");
        testStudent.setLevel(Level.FRESHMAN);

        testStudentDTO = new StudentDTO();
        testStudentDTO.setId(1L);
        testStudentDTO.setUsername("johndoe");
        testStudentDTO.setLevel(Level.FRESHMAN);
    }

    @Test
    void testList_WithoutFilters() {
        // Arrange
        List<Student> students = Arrays.asList(testStudent);
        Page<Student> page = new PageImpl<>(students);
        Pageable pageable = PageRequest.of(0, 10);

        when(studentRepository.findAll(pageable)).thenReturn(page);
        when(mapper.toDTO(testStudent)).thenReturn(testStudentDTO);

        // Act
        Page<StudentDTO> result = studentService.list(0, 10, null, null);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(studentRepository).findAll(pageable);
    }

    @Test
    void testList_WithSearchFilter() {
        // Arrange
        String search = "john";
        List<Student> students = Arrays.asList(testStudent);
        Page<Student> page = new PageImpl<>(students);
        Pageable pageable = PageRequest.of(0, 10);

        when(studentRepository.findByUsernameContainingIgnoreCase(search, pageable)).thenReturn(page);
        when(mapper.toDTO(testStudent)).thenReturn(testStudentDTO);

        // Act
        Page<StudentDTO> result = studentService.list(0, 10, search, null);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(studentRepository).findByUsernameContainingIgnoreCase(search, pageable);
    }

    @Test
    void testList_WithLevelFilter() {
        // Arrange
        Level level = Level.FRESHMAN;
        List<Student> students = Arrays.asList(testStudent);
        Page<Student> page = new PageImpl<>(students);
        Pageable pageable = PageRequest.of(0, 10);

        when(studentRepository.findByLevel(level, pageable)).thenReturn(page);
        when(mapper.toDTO(testStudent)).thenReturn(testStudentDTO);

        // Act
        Page<StudentDTO> result = studentService.list(0, 10, null, level);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(studentRepository).findByLevel(level, pageable);
    }

    @Test
    void testGet_Success() {
        // Arrange
        when(studentRepository.findById(1L)).thenReturn(Optional.of(testStudent));
        when(mapper.toDTO(testStudent)).thenReturn(testStudentDTO);

        // Act
        StudentDTO result = studentService.get(1L);

        // Assert
        assertNotNull(result);
        assertEquals("johndoe", result.getUsername());
        verify(studentRepository).findById(1L);
    }

    @Test
    void testGet_NotFound() {
        // Arrange
        when(studentRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> studentService.get(999L)
        );

        assertEquals("Student not found", exception.getMessage());
        verify(studentRepository).findById(999L);
    }

    @Test
    void testCreate_Success() {
        // Arrange
        StudentDTO newStudentDTO = new StudentDTO();
        newStudentDTO.setUsername("newstudent");
        newStudentDTO.setLevel(Level.SOPHOMORE);

        when(studentRepository.existsByUsername(newStudentDTO.getUsername())).thenReturn(false);
        when(studentRepository.save(any(Student.class))).thenReturn(testStudent);
        when(mapper.toDTO(testStudent)).thenReturn(testStudentDTO);

        // Act
        StudentDTO result = studentService.create(newStudentDTO);

        // Assert
        assertNotNull(result);
        verify(studentRepository).existsByUsername(newStudentDTO.getUsername());
        verify(studentRepository).save(any(Student.class));
    }

    @Test
    void testCreate_UsernameExists() {
        // Arrange
        StudentDTO newStudentDTO = new StudentDTO();
        newStudentDTO.setUsername("existinguser");
        newStudentDTO.setLevel(Level.JUNIOR);

        when(studentRepository.existsByUsername(newStudentDTO.getUsername())).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> studentService.create(newStudentDTO)
        );

        assertEquals("Student username exists", exception.getMessage());
        verify(studentRepository).existsByUsername(newStudentDTO.getUsername());
        verify(studentRepository, never()).save(any(Student.class));
    }

    @Test
    void testUpdate_Success() {
        // Arrange
        StudentDTO updateDTO = new StudentDTO();
        updateDTO.setUsername("updatedjohn");
        updateDTO.setLevel(Level.SENIOR);

        when(studentRepository.findById(1L)).thenReturn(Optional.of(testStudent));
        when(studentRepository.save(testStudent)).thenReturn(testStudent);
        when(mapper.toDTO(testStudent)).thenReturn(testStudentDTO);

        // Act
        StudentDTO result = studentService.update(1L, updateDTO);

        // Assert
        assertNotNull(result);
        verify(studentRepository).findById(1L);
        verify(studentRepository).save(testStudent);
    }

    @Test
    void testUpdate_NotFound() {
        // Arrange
        StudentDTO updateDTO = new StudentDTO();
        updateDTO.setUsername("updatedjohn");
        updateDTO.setLevel(Level.SENIOR);

        when(studentRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> studentService.update(999L, updateDTO)
        );

        assertEquals("Student not found", exception.getMessage());
        verify(studentRepository).findById(999L);
        verify(studentRepository, never()).save(any(Student.class));
    }

    @Test
    void testDelete_Success() {
        // Arrange
        when(studentRepository.existsById(1L)).thenReturn(true);
        doNothing().when(studentRepository).deleteById(1L);

        // Act
        studentService.delete(1L);

        // Assert
        verify(studentRepository).existsById(1L);
        verify(studentRepository).deleteById(1L);
    }

    @Test
    void testDelete_NotFound() {
        // Arrange
        when(studentRepository.existsById(999L)).thenReturn(false);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> studentService.delete(999L)
        );

        assertEquals("Student not found", exception.getMessage());
        verify(studentRepository).existsById(999L);
        verify(studentRepository, never()).deleteById(anyLong());
    }

    @Test
    void testSearch_Success() {
        // Arrange
        String searchTerm = "john";
        List<Student> students = Arrays.asList(testStudent);

        when(studentRepository.findByUsernameContainingIgnoreCase(searchTerm)).thenReturn(students);
        when(mapper.toDTO(testStudent)).thenReturn(testStudentDTO);

        // Act
        List<StudentDTO> result = studentService.search(searchTerm);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("johndoe", result.get(0).getUsername());
        verify(studentRepository).findByUsernameContainingIgnoreCase(searchTerm);
    }

    @Test
    void testList_DefaultPagination() {
        // Arrange
        List<Student> students = Arrays.asList(testStudent);
        Page<Student> page = new PageImpl<>(students);
        Pageable pageable = PageRequest.of(0, 10);

        when(studentRepository.findAll(pageable)).thenReturn(page);
        when(mapper.toDTO(testStudent)).thenReturn(testStudentDTO);

        // Act
        Page<StudentDTO> result = studentService.list(null, null, null, null);

        // Assert
        assertNotNull(result);
        verify(studentRepository).findAll(pageable);
    }
}
