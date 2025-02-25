This project is designed to manage users in a system with basic role-based access control. It includes functionalities for:

- **User Sign-up:** Allow user to sign up
- **User Sign-In:** Authenticate users with BCrypt password encoding.
- **User Listing:** Allow admin users to retrieve a list of all users.
- **User Deletion:** Enable admin users to delete a user by email.

The service leverages Spring Boot, along with JUnit and Mockito for testing the service layer and repository interactions.

## Features

- **Authentication:** Secure sign-in process using BCrypt hashing.
- **Role-based Access:** Admin endpoints that restrict access to authorized users.
- **RESTful API:** Standard HTTP responses with appropriate status codes and messages.
- **Unit Testing:** Comprehensive tests for service methods covering positive and negative cases.

## Prerequisites

Before running the project, ensure you have the following installed:

- **Java JDK 17**
- **Maven 3.6+** (or your preferred build tool)
- **Docker** (for containerized setup)
- An IDE (such as IntelliJ IDEA or Eclipse) for development

## Installation

1. **Clone the repository:**

   ```bash
   git clone git@github.com:divyendhsuresh/mykare_Assignemnt.git
   cd mykare_assignment
   docker compose up
   
   
   run this ddl script for table creation :

CREATE TABLE users (
id SERIAL PRIMARY KEY,
name VARCHAR(255) NOT NULL,
email VARCHAR(255) UNIQUE NOT NULL,
password VARCHAR(255) NOT NULL,
gender VARCHAR(50) NOT NULL CHECK (gender IN ('Male', 'Female', 'Other')),
ip_address VARCHAR(45) NULL,
country VARCHAR(100) NULL,
role VARCHAR(10) NOT NULL CHECK (role IN ('USER', 'ADMIN')),
created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


Can Access Swagger : http://localhost:8080/swagger-ui/index.html#/controller/registerUser 

signup req : http://localhost:8080/auth/signup

Signup request example : {
"name": "Deepu",
"email": "user@example.com",
"gender": "Male",
"password": "userPass",
"role":"USER"/"ADMIN"
}
with headers as Content-Type value : application/json

SIgn in request : http://localhost:8080/auth/signin

Sign in request example : {
"email":"user@example.com",
"password":"userPass"
}

get all data from db(only admin) : http://localhost:8080/auth/allusers?email=
this request need basic auth Username:user Password:password
and params email:admin email id

Delet data using mail id :http://localhost:8080/auth/delete?adminEmail=&userEmail=
this request need basic auth Username:user Password:password
and params as : adminEmail : adminemail
and userEmail : user email 
