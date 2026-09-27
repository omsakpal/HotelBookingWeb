[README.md](https://github.com/user-attachments/files/32710772/README.md)
# THE WELLESLEY — Hotel Booking Web Application

A luxury hotel booking web application built with **Spring Boot, Thymeleaf, MySQL, and Java**. The system allows customers to register, verify their email using OTP, browse hotel rooms, make bookings, view their bookings, cancel bookings, and manage their accounts.

## Features

- Customer registration with email OTP verification
- Secure password storage using BCrypt
- Customer login and logout
- Forgot-password and password-reset flow with OTP verification
- Hotel home page
- Browse available hotel rooms
- Room details with category, bed type, capacity, price, and amenities
- Room amenities including spa, pool, and balcony
- Hotel room booking with check-in and check-out dates
- Guest selection and automatic bill calculation
- Booking confirmation page
- Booking confirmation email
- View current and previous bookings
- Cancel bookings
- Customer account management
- Delete customer account
- MySQL database integration
- GitHub-safe configuration using `application.properties.example`

## Technology Stack

| Technology | Purpose |
|---|---|
| Java 21 | Application development |
| Spring Boot 4.1.1 | Backend framework |
| Spring MVC | Web request handling |
| Thymeleaf | Server-side HTML rendering |
| Spring Data JPA | Database access |
| MySQL 8.0 | Database |
| Spring Mail | Email and OTP delivery |
| BCrypt | Password hashing |
| Maven | Project and dependency management |
| HTML / CSS | Frontend |

## Project Structure

```text
HotelBookingWeb/
├── HotelBookingDatabase.sql
├── HotelBooking_Admin_Queries.sql
├── pom.xml
├── mvnw
├── mvnw.cmd
├── .gitignore
└── src/
    └── main/
        ├── java/
        │   └── com/hotel/hotelbooking/
        │       ├── HotelBookingWebApplication.java
        │       ├── PasswordConfig.java
        │       ├── controller/
        │       │   └── HomeController.java
        │       ├── entity/
        │       │   ├── Booking.java
        │       │   ├── Customer.java
        │       │   └── Room.java
        │       ├── repository/
        │       │   ├── BookingRepository.java
        │       │   ├── CustomerRepository.java
        │       │   └── RoomRepository.java
        │       └── service/
        │           ├── EmailService.java
        │           └── OtpService.java
        │
        └── resources/
            ├── application.properties.example
            ├── static/
            │   └── images/
            └── templates/
```

## Database

The project uses a MySQL database named:

```text
hotelbooking
```

Main tables:

- `customers` — stores customer account information
- `rooms` — stores hotel room information and availability
- `bookings` — stores customer booking records

The database setup script is available in:

```text
HotelBookingDatabase.sql
```

Administrative SQL queries are available in:

```text
HotelBooking_Admin_Queries.sql
```

## Database Setup

1. Install **MySQL 8.0** and MySQL Workbench.
2. Open `HotelBookingDatabase.sql` in MySQL Workbench.
3. Execute the complete script.
4. Confirm that the `hotelbooking` database and its tables have been created.

## Application Configuration

The real `application.properties` file is intentionally excluded from GitHub because it contains local database and email credentials.

A safe template is provided as:

```text
src/main/resources/application.properties.example
```

Create your local configuration file:

```text
src/main/resources/application.properties
```

and configure your own MySQL and Gmail SMTP credentials.

Example structure:

```properties
spring.application.name=HotelBookingWeb

spring.datasource.url=jdbc:mysql://localhost:3306/hotelbooking
spring.datasource.username=YOUR_MYSQL_USERNAME
spring.datasource.password=YOUR_MYSQL_PASSWORD
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=true

spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=YOUR_GMAIL_ADDRESS
spring.mail.password=YOUR_GMAIL_APP_PASSWORD
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

**Never commit your real `application.properties` or passwords to GitHub.**

## Running the Application

### Using IntelliJ IDEA

1. Open the project in IntelliJ IDEA.
2. Make sure Java 21 is selected.
3. Make sure MySQL is running.
4. Configure `application.properties`.
5. Run:

```text
HotelBookingWebApplication
```

6. Open the application in a browser:

```text
http://localhost:8080
```

### Using Maven

On Windows:

```bash
mvnw.cmd spring-boot:run
```

Then open:

```text
http://localhost:8080
```

## Application Flow

```text
Registration
     ↓
Email OTP Verification
     ↓
Registration Details
     ↓
Login
     ↓
Home
     ↓
Browse Rooms
     ↓
Room Details
     ↓
Booking
     ↓
Booking Confirmation
     ↓
Confirmation Email
     ↓
My Bookings
     ↓
Cancellation / Account Management
```

## Security

The project follows basic security practices for a college-level web application:

- Passwords are stored using **BCrypt hashing**.
- Email verification uses time-limited OTPs.
- Database and email credentials are kept in the local `application.properties`.
- `application.properties` is excluded using `.gitignore`.
- `application.properties.example` contains only placeholder values.
- No real customer records are included in the database seed script.

## GitHub

Repository:

**HotelBookingWeb**

The repository contains the source code, database setup scripts, frontend templates, static assets, and safe configuration examples.

## Future Scope

Possible improvements for a production-ready version include:

- Online payment integration
- Admin dashboard
- Advanced room search and filtering
- Real-time room availability
- Customer reviews and ratings
- Booking modification
- Cloud deployment
- Improved authentication and authorization
- Responsive mobile-first design

## Project

**Project Name:** THE WELLESLEY  
**Type:** Hotel Booking Web Application  
**Backend:** Spring Boot  
**Frontend:** Thymeleaf, HTML, CSS  
**Database:** MySQL  
**Language:** Java
