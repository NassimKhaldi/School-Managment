import { Component, OnInit } from '@angular/core';
import { StudentService } from '../../services/student.service';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';
import { Student } from '../../models/models';

@Component({
  selector: 'app-students',
  templateUrl: './students.component.html',
  styleUrls: ['./students.component.css'],
})
export class StudentsComponent implements OnInit {
  students: Student[] = [];
  totalElements = 0;
  page = 0;
  size = 10;
  searchTerm = '';
  selectedLevel = '';
  levels = ['FRESHMAN', 'SOPHOMORE', 'JUNIOR', 'SENIOR'];
  Math = Math;

  showModal = false;
  editMode = false;
  currentStudent: Student = { username: '', level: 'FRESHMAN' };
  error = '';
  message = '';

  constructor(
    private studentService: StudentService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadStudents();
  }

  loadStudents(): void {
    this.studentService
      .getStudents(
        this.page,
        this.size,
        this.searchTerm || undefined,
        this.selectedLevel || undefined
      )
      .subscribe({
        next: (response) => {
          this.students = response.content;
          this.totalElements = response.totalElements;
        },
        error: (err) => {
          if (err.status === 401 || err.status === 403) {
            this.error = 'Session expired. Please login again.';
            setTimeout(() => {
              this.authService.logout();
              this.router.navigate(['/login']);
            }, 2000);
          } else {
            this.error = 'Failed to load students';
          }
        },
      });
  }

  onSearch(): void {
    this.page = 0;
    this.loadStudents();
  }

  onFilter(): void {
    this.page = 0;
    this.loadStudents();
  }

  nextPage(): void {
    if ((this.page + 1) * this.size < this.totalElements) {
      this.page++;
      this.loadStudents();
    }
  }

  previousPage(): void {
    if (this.page > 0) {
      this.page--;
      this.loadStudents();
    }
  }

  openCreateModal(): void {
    this.editMode = false;
    this.currentStudent = { username: '', level: 'FRESHMAN' };
    this.showModal = true;
    this.error = '';
  }

  openEditModal(student: Student): void {
    this.editMode = true;
    this.currentStudent = { ...student };
    this.showModal = true;
    this.error = '';
  }

  closeModal(): void {
    this.showModal = false;
    this.currentStudent = { username: '', level: 'FRESHMAN' };
    this.error = '';
  }

  saveStudent(): void {
    if (!this.currentStudent.username) {
      this.error = 'Username is required';
      return;
    }

    if (this.editMode && this.currentStudent.id) {
      this.studentService
        .updateStudent(this.currentStudent.id, this.currentStudent)
        .subscribe({
          next: () => {
            this.message = 'Student updated successfully';
            this.closeModal();
            this.loadStudents();
            setTimeout(() => (this.message = ''), 3000);
          },
          error: (err) => {
            this.error = err.error?.message || 'Failed to update student';
          },
        });
    } else {
      this.studentService.createStudent(this.currentStudent).subscribe({
        next: () => {
          this.message = 'Student created successfully';
          this.closeModal();
          this.loadStudents();
          setTimeout(() => (this.message = ''), 3000);
        },
        error: (err) => {
          this.error = err.error?.message || 'Failed to create student';
        },
      });
    }
  }

  deleteStudent(id: number | undefined): void {
    if (!id || !confirm('Are you sure you want to delete this student?'))
      return;

    this.studentService.deleteStudent(id).subscribe({
      next: () => {
        this.message = 'Student deleted successfully';
        this.loadStudents();
        setTimeout(() => (this.message = ''), 3000);
      },
      error: (err) => {
        this.error = err.error?.message || 'Failed to delete student';
      },
    });
  }

  exportCsv(): void {
    this.studentService
      .exportCsv(this.searchTerm || undefined, this.selectedLevel || undefined)
      .subscribe({
        next: (blob) => {
          const url = window.URL.createObjectURL(blob);
          const a = document.createElement('a');
          a.href = url;
          a.download = 'students.csv';
          a.click();
          window.URL.revokeObjectURL(url);
          this.message = 'CSV exported successfully';
          setTimeout(() => (this.message = ''), 3000);
        },
        error: () => {
          this.error = 'Failed to export CSV';
        },
      });
  }

  onFileSelected(event: any): void {
    const file: File = event.target.files[0];
    if (file) {
      this.studentService.importCsv(file).subscribe({
        next: (response) => {
          this.message = response;
          this.loadStudents();
          event.target.value = '';
          setTimeout(() => (this.message = ''), 3000);
        },
        error: () => {
          this.error = 'Failed to import CSV';
          event.target.value = '';
        },
      });
    }
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
