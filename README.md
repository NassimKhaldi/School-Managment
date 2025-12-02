# School Management System

A full-stack web application for managing student records with secure authentication, built with **Spring Boot** (backend) and **Angular** (frontend).

## 🚀 Features

### Backend (Spring Boot)

- **JWT Authentication** - Secure login and registration for admin users
- **Student CRUD Operations** - Complete Create, Read, Update, Delete functionality
- **Advanced Search & Filtering** - Search by username/ID and filter by level
- **Pagination** - Efficient data handling with paginated results
- **CSV Export/Import** - Bulk operations for student data
- **Swagger Documentation** - Interactive API documentation
- **Global Exception Handling** - Standardized error responses
- **Input Validation** - Server-side validation with proper error messages
- **Rate Limiting** - Protection against brute-force attacks (5 login attempts per minute)

### Frontend (Angular)

- **Responsive UI** - Clean and intuitive user interface
- **Login/Registration** - Admin authentication with JWT token management
- **Student Management** - View, create, edit, and delete students
- **Search & Filter** - Real-time search and level-based filtering
- **Pagination Controls** - Easy navigation through student records
- **CSV Operations** - Export and import student data
- **Route Guards** - Protected routes requiring authentication

## 📋 Prerequisites

Before running this project, ensure you have the following installed:

- **Docker** (version 20.10 or higher)
- **Docker Compose** (version 2.0 or higher)

That's it! Docker will handle all dependencies including Java, Node.js, PostgreSQL, etc.

## 🏃 How to Run the Project

### Using Docker Compose (Recommended)

1. **Clone the repository**

   ```bash
   git clone https://github.com/NassimKhaldi/School-Managment.git
   cd School-Managment
   ```

2. **Start all services**

   ```bash
   docker-compose up --build
   ```

   This command will:

   - Build the Spring Boot backend
   - Build the Angular frontend
   - Start PostgreSQL database
   - Connect all services automatically

3. **Access the application**

   - **Frontend**: http://localhost:4200
   - **Backend API**: http://localhost:8081/api
   - **Swagger UI**: http://localhost:8081/swagger-ui.html

4. **Stop the application**

   ```bash
   docker-compose down
   ```

   To remove volumes (database data):

   ```bash
   docker-compose down -v
   ```

### Running Locally (Without Docker)

#### Backend

```bash
cd sm-backend
./mvnw spring-boot:run
```

**Note**: Requires Java 17, Maven, and PostgreSQL installed locally.

#### Frontend

```bash
cd sm-frontend
npm install
npm start
```

**Note**: Requires Node.js 18+ and npm installed locally.

## 🔑 Default Credentials

When you first start the application, you'll need to **register** an admin account.

Use the registration form on the login page:

- Username: `admin` (or any unique username)
- Password: `admin123` (minimum 6 characters)

## 📊 Database Schema

### Admin Entity

| Field    | Type   | Constraints       |
| -------- | ------ | ----------------- |
| id       | Long   | Primary Key, Auto |
| username | String | Unique, Not Null  |
| password | String | Hashed, Not Null  |

### Student Entity

| Field    | Type   | Constraints                         |
| -------- | ------ | ----------------------------------- |
| id       | Long   | Primary Key, Auto                   |
| username | String | Unique, Not Null                    |
| level    | Enum   | FRESHMAN, SOPHOMORE, JUNIOR, SENIOR |

## 🛠️ Technology Stack

### Backend

- **Framework**: Spring Boot 4.0.0
- **Security**: Spring Security + JWT (JJWT 0.11.5)
- **Database**: PostgreSQL 15
- **ORM**: Spring Data JPA / Hibernate
- **Validation**: Jakarta Validation
- **Documentation**: Springdoc OpenAPI 3
- **Build Tool**: Maven
- **Java Version**: 17

### Frontend

- **Framework**: Angular 16
- **Language**: TypeScript
- **HTTP Client**: Angular HttpClient
- **Routing**: Angular Router
- **Forms**: Reactive Forms
- **UI**: Custom CSS with animations

### DevOps

- **Containerization**: Docker
- **Orchestration**: Docker Compose
- **Web Server**: Nginx (for Angular)

## 📡 API Endpoints

### Authentication

| Method | Endpoint             | Description        | Auth Required |
| ------ | -------------------- | ------------------ | ------------- |
| POST   | `/api/auth/register` | Register new admin | ❌            |
| POST   | `/api/auth/login`    | Login and get JWT  | ❌            |

### Students (All require JWT)

| Method | Endpoint               | Description               | Auth Required |
| ------ | ---------------------- | ------------------------- | ------------- |
| GET    | `/api/students`        | List students (paginated) | ✅            |
| GET    | `/api/students/{id}`   | Get student by ID         | ✅            |
| POST   | `/api/students`        | Create new student        | ✅            |
| PUT    | `/api/students/{id}`   | Update student            | ✅            |
| DELETE | `/api/students/{id}`   | Delete student            | ✅            |
| GET    | `/api/students/search` | Search by username        | ✅            |
| GET    | `/api/students/export` | Export students to CSV    | ✅            |
| POST   | `/api/students/import` | Import students from CSV  | ✅            |

### Query Parameters

- `page` - Page number (default: 0)
- `size` - Page size (default: 10)
- `search` - Search term for username/ID
- `level` - Filter by level (FRESHMAN, SOPHOMORE, JUNIOR, SENIOR)

## 🔒 Security Features

1. **Password Hashing** - All passwords encrypted using BCrypt
2. **JWT Authentication** - Stateless token-based authentication
3. **Rate Limiting** - Maximum 5 login attempts per minute per IP
4. **Input Validation** - Server-side validation for all inputs
5. **CORS Configuration** - Restricted cross-origin requests
6. **Protected Routes** - Student APIs require valid JWT token

## 📝 HTTP Status Codes

| Code | Name                  | Usage                                         |
| ---- | --------------------- | --------------------------------------------- |
| 200  | OK                    | Successful GET/PUT/DELETE                     |
| 201  | Created               | Successfully created resource                 |
| 400  | Bad Request           | Invalid input or validation error             |
| 401  | Unauthorized          | Missing or invalid JWT token                  |
| 404  | Not Found             | Resource doesn't exist                        |
| 409  | Conflict              | Duplicate username or resource already exists |
| 429  | Too Many Requests     | Rate limit exceeded                           |
| 500  | Internal Server Error | Unexpected server error                       |

## 🧪 Testing

### Run Backend Tests

```bash
cd sm-backend
./mvnw test
```

Tests include:

- Unit tests for AuthService (login, registration)
- Unit tests for StudentService (CRUD operations)
- Integration tests for repositories

## 📁 Project Structure

```
School-Managment/
├── sm-backend/                 # Spring Boot Backend
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/school/management/smbackend/
│   │   │   │   ├── Config/           # Security & CORS config
│   │   │   │   ├── Controllers/      # REST endpoints
│   │   │   │   ├── DTOs/             # Data Transfer Objects
│   │   │   │   ├── Entities/         # JPA entities
│   │   │   │   ├── Exception/        # Global exception handler
│   │   │   │   ├── Mappers/          # Entity-DTO mappers
│   │   │   │   ├── Repositories/     # Data access layer
│   │   │   │   ├── Security/         # JWT utilities
│   │   │   │   └── Services/         # Business logic
│   │   │   └── resources/
│   │   │       └── application.properties
│   │   └── test/                     # Unit & integration tests
│   ├── Dockerfile
│   └── pom.xml
│
├── sm-frontend/               # Angular Frontend
│   ├── src/
│   │   ├── app/
│   │   │   ├── components/   # UI components
│   │   │   ├── guards/       # Route guards
│   │   │   ├── interceptors/ # HTTP interceptors
│   │   │   ├── models/       # TypeScript interfaces
│   │   │   └── services/     # API services
│   │   └── assets/
│   ├── Dockerfile
│   ├── nginx.conf
│   └── package.json
│
└── docker-compose.yml         # Orchestration file
```

## 🐛 Troubleshooting

### Port Already in Use

If you get port conflicts, modify the ports in `docker-compose.yml`:

```yaml
ports:
  - "8081:8080" 
  - "4200:80" 
```

### Database Connection Issues

Ensure PostgreSQL container is healthy:

```bash
docker-compose logs database
```

### Backend Not Starting

Check backend logs:

```bash
docker-compose logs backend
```

### Frontend Not Loading

Verify nginx configuration:

```bash
docker-compose logs frontend
```

## 📚 Additional Notes

- **First Run**: Database tables are created automatically on first startup
- **Data Persistence**: Student data is stored in Docker volumes and persists between restarts
- **Development Mode**: For active development, run backend and frontend locally without Docker
- **API Documentation**: Full interactive API docs available at `/swagger-ui.html`
- **CSV Format**: Import files should have header: `id,username,level`

## 📄 License

This project is created as a technical test for an internship application.

