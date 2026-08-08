# 🚀 Flow Deck – Enterprise Task Management System

Flow Deck is a modern Enterprise Task Management System built using **Spring Boot**. It helps organizations manage projects, tasks, employees, and project managers through a secure Role-Based Access Control (RBAC) system.

The application provides JWT Authentication, OTP-based verification, Project Management, Task Tracking, and Employee Collaboration in a scalable enterprise architecture.

---

# ✨ Features

## 🔐 Authentication & Security

- JWT Authentication
- Spring Security
- Role-Based Access Control (RBAC)
- OTP Email Verification
- Forgot Password
- Password Reset
- BCrypt Password Encryption

---

## 👨‍💼 Admin Module

- Manage Users
- Manage Roles
- Manage Departments
- Manage Designations
- Manage Projects
- Assign Project Managers
- Activate / Deactivate Users
- Search Users
- View Project Details

---

## 📁 Project Manager Module

- View Assigned Projects
- Manage Project Members
- Create Tasks
- Update Tasks
- Delete Tasks
- Assign/Reassign Tasks
- Track Project Progress
- Monitor Team Performance
- View Project Statistics

---

## 👨‍💻 Employee Module

- View Assigned Projects
- View Assigned Tasks
- Update Task Status
- Add/Edit/Delete Task Comments
- Personal Dashboard
- Manage Own Profile
- Change Password

---

# 🛠 Tech Stack

## Backend

- Java 21
- Spring Boot 3
- Spring Security
- Spring Data JPA
- Hibernate
- Maven

## Database

- MySQL

## Authentication

- JWT
- BCrypt Password Encoder

## Documentation

- Swagger / OpenAPI 3

## Email Service

- Spring Mail (SMTP)

---

# 📂 Project Structure

```
src
├── config
├── controller
├── dto
├── entity
├── exception
├── mapper
├── repository
├── security
├── service
├── util
└── resources
```

---

# 🔑 Roles

| Role | Responsibilities |
|------|------------------|
| Admin | User, Role, Department, Designation and Project Management |
| Project Manager | Manage Team Members, Tasks and Assigned Projects |
| Employee | View Assigned Tasks, Update Status, Add Comments |

---

# 🔄 Application Workflow

```
User Registration
        │
        ▼
Email OTP Verification
        │
        ▼
Login
        │
        ▼
JWT Token Generated
        │
        ▼
Role Based Access
        │
 ┌──────┼────────┐
 │      │        │
 ▼      ▼        ▼
Admin   Project  Employee
        Manager
```

---

# 🔐 Security

- JWT Authentication
- Stateless Authentication
- Role-Based Authorization
- Method-Level Security
- Global Exception Handling
- Password Encryption using BCrypt

---

# 📖 API Documentation

Swagger UI

```
http://localhost:8080/swagger-ui.html
```

OpenAPI

```
http://localhost:8080/v3/api-docs
```

---

# ⚙️ Installation

## Clone Repository

```bash
git clone https://github.com/LakshyaSoni19/Flow-Deck-.git
```

---

## Navigate

```bash
cd Flow-Deck-
```

---

## Configure Database

Create MySQL Database

```sql
CREATE DATABASE flow_deck_db;
```

Update credentials inside your local configuration or environment variables.

---

## Run Application

```bash
./mvnw spring-boot:run
```

or

```bash
mvn spring-boot:run
```

---

# 📬 Environment Variables

Configure the following variables before running the application.

| Variable | Description |
|----------|-------------|
| DB_USERNAME | Database Username |
| DB_PASSWORD | Database Password |
| MAIL_USERNAME | Gmail Username |
| MAIL_PASSWORD | Gmail App Password |
| JWT_SECRET | JWT Secret Key |

---

# 🧪 Testing

```bash
./mvnw test
```

---

# 📌 Future Enhancements

- React Frontend
- File Upload
- Notifications
- Dashboard Charts
- Reports & Analytics
- Audit Logs
- Docker Support
- CI/CD Pipeline
- Kubernetes Deployment

---

# 👨‍💻 Author

**Lakshya Soni**

Java Full Stack Developer

GitHub:
https://github.com/LakshyaSoni19

LinkedIn:
https://www.linkedin.com/in/lakshya-soni-736742285/

---

# ⭐ Support

If you like this project, consider giving it a ⭐ on GitHub.
