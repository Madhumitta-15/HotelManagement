# 🏨 Smart Hotel Management System

A modern full-stack hotel booking and management application built with **Spring Boot** (Java backend) and **React.js** (frontend with Tailwind CSS & pnpm).

---

## 🚀 Overview

This system allows:

* Guests to search and book hotel rooms.
* **Email Notifications**: Automated emails sent for booking confirmations and new user reviews.
* **Hotels Displayed by Rating**: Hotels on the landing page are ordered by their average customer rating (highest first).
* **Managers** to manage rooms, room availability, and room images for their assigned hotels.
* **Admins** to manage hotels, add new managers, and assign managers to specific hotels.
* Basic **mock payment simulation** without third-party integration.

---

## 🧰 Tech Stack

| Layer      | Technology                                 |
| :--------- | :----------------------------------------- |
| Frontend   | React.js, Tailwind CSS, pnpm               |
| Backend    | Spring Boot, Spring Security, JPA          |
| Database   | MySQL                                      |
| Auth       | JWT (JSON Web Tokens)                      |
| Payments   | Simulated (no external gateways)           |
| Docs       | SpringDoc OpenAPI (Swagger)                |
| Tools      | Maven (backend), pnpm (frontend)           |
| Email      | JavaMailSender (or similar for Spring Boot)|

---

## ⚙️ Prerequisites

Before you begin, ensure you have the following installed:

* **Java 21+**
* **Node.js 18+**
* **MySQL 8+**
* [**pnpm**](https://pnpm.io/)
* **Maven**
* **Git**
* **Email Service Configuration**: Access to an SMTP server (e.g., Gmail, SendGrid) for sending emails.

---

## 🔧 Getting Started

Follow these steps to get the Smart Hotel Management System up and running on your local machine.

### Backend Setup

1.  **Navigate to the backend directory:**

    ```bash
    cd hotelmanagement_backend
    ```

2.  **Update `application.properties`:**

    Open the `src/main/resources/application.properties` file and configure your database connection, server port, and **email service details**:

    ```properties
    server.port=8095

    spring.datasource.url=jdbc:mysql://localhost:3306/hotel_db
    spring.datasource.username=root
    spring.datasource.password=yourpassword
    spring.jpa.hibernate.ddl-auto=update

    # Email Configuration (Example for Gmail SMTP)
    spring.mail.host=smtp.gmail.com
    spring.mail.port=587
    spring.mail.username=your-email@gmail.com
    spring.mail.password=your-email-password # Use app-specific password if 2FA is on
    spring.mail.properties.mail.smtp.auth=true
    spring.mail.properties.mail.smtp.starttls.enable=true
    # For less secure app access (not recommended for production):
    # spring.mail.properties.mail.smtp.ssl.trust=smtp.gmail.com
    ```

    * **`server.port`**: The port where your Spring Boot backend will run.
    * **`spring.datasource.url`**: Your MySQL database connection URL. Make sure `hotel_db` exists or will be created by Hibernate.
    * **`spring.datasource.username`**: Your MySQL username.
    * **`spring.datasource.password`**: Your MySQL password.
    * **`spring.jpa.hibernate.ddl-auto`**: Set to `update` to allow Hibernate to manage schema updates.
    * **Email Configuration**: **Crucial for email features.** Replace `your-email@gmail.com` and `your-email-password` with your actual email credentials. If using Gmail with 2-Factor Authentication, you'll need to generate an [App Password](https://support.google.com/accounts/answer/185833?hl=en) for this purpose.

3.  **Run the Spring Boot application:**

    You can run the backend using Maven:

    ```bash
    mvn spring-boot:run
    ```

    The backend server will start and be accessible at `http://localhost:8095`.

---

### 🌐 Frontend Setup

1.  **Navigate to the frontend folder:**

    ```bash
    cd hotelmanagement_frontend
    ```

2.  **Install dependencies:**

    Install all required Node.js packages using pnpm:

    ```bash
    pnpm install
    ```

    This command will install the following key dependencies:
    * `react-router-dom` for client-side routing.
    * `react-toastify` and `react-hot-toast` for notifications.
    * `axios` for making HTTP requests.
    * `react-datepicker` for date input.
    * `jwt-decode` for decoding JSON Web Tokens.
    * `uuid` for generating unique IDs.
    * `framer-motion` for animations.
    * `@fortawesome/react-fontawesome` and `@fortawesome/free-solid-svg-icons` for icons.

3.  **Start development server:**

    ```bash
    pnpm run dev
    ```

    The frontend development server will start, typically running at:

    📌 **Runs at: `http://localhost:5173`**

    **Important:** Ensure your React API calls are configured to hit the backend at `http://localhost:8095`. You might need to check your frontend's environment configuration or API utility files to confirm this.

---

## ▶️ Usage

Once both the backend and frontend are running, you can access the application through your web browser.

### Guest Features (`http://localhost:5173`)

* **Landing Page**: The main landing page will display hotels ordered from the highest average customer rating to the lowest, helping users discover top-rated accommodations first.
* **Search & Book Rooms**: Guests can browse available rooms, view details, and proceed with the mock booking process.
* **Booking Confirmation Email**: Upon successful booking, the user will receive an automated email notification confirming their reservation details.
* **Add Reviews**: Users can submit reviews for hotels they have stayed in.
* **Review Confirmation Email**: After a user submits a review, they will receive an email acknowledging their contribution.

### Manager Dashboard Features (Accessible after Manager Login)

Managers will have access to a dedicated dashboard for managing their assigned hotel's rooms:

* **Add Rooms**: Managers can add new room types, including details like room number, capacity, price, and amenities.
* **Manage Room Availability**: Managers can update the availability status of rooms for specific dates.
* **Upload Room Images**: Managers can upload and associate images with individual rooms, which will be displayed to guests.
* **View Rooms**: Managers can view a comprehensive list of all rooms under their hotel, along with their details, availability, and associated images.

### Admin Dashboard Features (Accessible after Admin Login)

Admins will have a central dashboard for system-wide management:

* **Add Hotels**: Admins can register new hotels into the system, providing essential details for each property.
* **Add Managers**: Admins can create new manager accounts, assigning them specific roles and credentials.
* **Assign Managers to Hotels**: Admins can assign newly created or existing managers to specific hotels, granting them control over that hotel's operations.
* **View Managers**: Admins can view a list of all registered managers and their assigned hotels.
* **View Hotels**: Admins can view a list of all registered hotels in the system.

---
