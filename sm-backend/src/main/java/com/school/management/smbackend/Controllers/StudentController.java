package com.school.management.smbackend.Controllers;

import com.school.management.smbackend.DTOs.StudentDTO;
import com.school.management.smbackend.Entities.Level;
import com.school.management.smbackend.Services.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/students")
@Tag(name = "Students", description = "Student CRUD APIs")
public class StudentController {
    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping
    @Operation(summary = "List students with pagination and filters")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    public Page<StudentDTO> list(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Level level
    ) {
        return studentService.list(page, size, search, level);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a student by id")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "404", description = "Not Found")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    public StudentDTO get(@PathVariable Long id) { return studentService.get(id); }

    @PostMapping
    @Operation(summary = "Create a student")
    @ApiResponse(responseCode = "201", description = "Created")
    @ApiResponse(responseCode = "400", description = "Bad Request")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "409", description = "Conflict")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    public ResponseEntity<StudentDTO> create(@Valid @RequestBody StudentDTO dto) {
        StudentDTO created = studentService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a student")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "400", description = "Bad Request")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "404", description = "Not Found")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    public StudentDTO update(@PathVariable Long id, @Valid @RequestBody StudentDTO dto) {
        return studentService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a student")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "404", description = "Not Found")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    public void delete(@PathVariable Long id) { studentService.delete(id); }

    @GetMapping("/search")
    @Operation(summary = "Search students by username")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    public List<StudentDTO> search(@RequestParam String username) {
        return studentService.search(username);
    }

    @GetMapping(value = "/export", produces = "text/csv")
    @Operation(summary = "Export students as CSV")
    @ApiResponse(responseCode = "200", description = "OK")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "500", description = "Internal Server Error")
    public ResponseEntity<byte[]> exportCsv(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Level level
    ) throws IOException {
        Page<StudentDTO> page = studentService.list(0, Integer.MAX_VALUE, search, level);
        StringBuilder sb = new StringBuilder("id,username,level\n");
        page.getContent().forEach(s -> sb.append(s.getId()).append(',')
                .append(s.getUsername()).append(',')
                .append(s.getLevel()).append('\n'));
        byte[] content = sb.toString().getBytes(StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=students.csv")
                .body(content);
    }

    @PostMapping(value = "/import", consumes = "multipart/form-data")
    @Operation(summary = "Import students from CSV")
    @ApiResponse(responseCode = "200", description = "Imported")
    @ApiResponse(responseCode = "400", description = "Bad Request")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @ApiResponse(responseCode = "500", description = "Internal Server Error")
    public ResponseEntity<String> importCsv(@RequestPart("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) return ResponseEntity.badRequest().body("Empty file");
        int created = 0; int updated = 0;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String line = reader.readLine(); // header
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 2) continue;
                StudentDTO dto = new StudentDTO();
                dto.setUsername(parts[1].trim());
                if (parts.length > 2) dto.setLevel(Level.valueOf(parts[2].trim()));
                try {
                    studentService.create(dto); created++;
                } catch (IllegalArgumentException ex) {
                    updated++;
                }
            }
        }
        return ResponseEntity.ok("Imported: " + created + ", Skipped/Updated: " + updated);
    }
}
