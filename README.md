# 🏨 Smart Hotel Management System

A modern full-stack hotel booking and management application built with **Spring Boot** (Java backend) and **React.js** (frontend with Tailwind CSS & pnpm).

---

## 🚀 Overview

This system allows:
- Guests to search and book hotel rooms.
- Admins to manage bookings, rooms, and users.
- Basic **mock payment simulation** without third-party integration.

---

## 🧰 Tech Stack

| Layer       | Technology                                |
|-------------|--------------------------------------------|
| Frontend    | React.js, Tailwind CSS, pnpm               |
| Backend     | Spring Boot, Spring Security, JPA          |
| Database    | MySQL                                      |
| Auth        | JWT (JSON Web Tokens)                      |
| Payments    | Simulated (no external gateways)           |
| Docs        | SpringDoc OpenAPI (Swagger)                |
| Tools       | Maven (backend), pnpm (frontend)           |

---

## 📂 Project Structure

HotelManagement/
├── hotelmanagement_backend/ # Spring Boot backend
│ ├── src/main/java/com/hotel/Booking_System
│ │ ├── controller/
│ │ ├── service/
│ │ ├── model/
│ │ └── repository/
│ └── application.properties
└── hotelmanagement_frontend/ # React frontend
├── src/
└── pnpm-lock.yaml

---

## ⚙️ Prerequisites

- Java 21+
- Node.js 18+
- MySQL 8+
- [pnpm](https://pnpm.io/)
- Maven
- Git

---

## 🔧 Backend Setup

1. Navigate to the backend directory:

```bash```
cd hotelmanagement_backend

2.Update application.properties:
properties
server.port=8095

spring.datasource.url=jdbc:mysql://localhost:3306/hotel_db
spring.datasource.username=root
spring.datasource.password=yourpassword
spring.jpa.hibernate.ddl-auto=update
---

## 🌐 Frontend Setup
Navigate to the frontend folder:
cd hotelmanagement_frontend
Install dependencies:
react-router-dom
react-toastify
react-hot-toast
axios
react-datepicker
jwt-decode
uuid
framer-motion
@fortawesome/react-fontawesome
@fortawesome/free-solid-svg-icons

pnpm install
Start development server:
pnpm run dev
📌 Runs at: http://localhost:5173
Make sure React API calls use http://localhost:8095
