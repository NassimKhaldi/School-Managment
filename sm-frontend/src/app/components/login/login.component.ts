import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { AnimationOptions } from 'ngx-lottie';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
})
export class LoginComponent {
  username = '';
  password = '';
  confirmPassword = '';
  error = '';
  message = '';
  loading = false;
  isRegisterMode = false;

  lottieOptions: AnimationOptions = {
    path: '/assets/login-animation.json',
    loop: true,
    autoplay: true,
  };

  constructor(private authService: AuthService, private router: Router) {}

  toggleMode(): void {
    this.isRegisterMode = !this.isRegisterMode;
    this.error = '';
    this.message = '';
    this.password = '';
    this.confirmPassword = '';
  }

  onSubmit(): void {
    if (!this.username || !this.password) {
      this.error = 'Username and password are required';
      return;
    }

    if (this.isRegisterMode) {
      if (this.password.length < 6) {
        this.error = 'Password must be at least 6 characters';
        return;
      }
      if (this.password !== this.confirmPassword) {
        this.error = 'Passwords do not match';
        return;
      }
      this.register();
    } else {
      this.login();
    }
  }

  login(): void {
    this.loading = true;
    this.error = '';
    this.authService
      .login({ username: this.username, password: this.password })
      .subscribe({
        next: () => {
          this.router.navigate(['/students']);
        },
        error: (err) => {
          this.loading = false;
          this.error =
            err.error?.message ||
            'Login failed. Please check your credentials.';
        },
      });
  }

  register(): void {
    this.loading = true;
    this.error = '';
    this.authService
      .register({ username: this.username, password: this.password })
      .subscribe({
        next: () => {
          this.loading = false;
          this.message = 'Registration successful! You can now login.';
          this.isRegisterMode = false;
          this.password = '';
          this.confirmPassword = '';
          setTimeout(() => (this.message = ''), 3000);
        },
        error: (err) => {
          this.loading = false;
          this.error =
            err.error?.message ||
            'Registration failed. Username may already exist.';
        },
      });
  }
}
