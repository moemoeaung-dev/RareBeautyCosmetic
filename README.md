# Rare Beauty Cosmetic Shop Ordering and Management System

A Java-based desktop application developed as a **fourth-year university project** for managing cosmetic products, customers, inventory, and orders for a cosmetic shop.

The system provides separate functionality for **customers and administrators**, including product browsing, shopping cart management, ordering, inventory management, product management, and user management.

---

## 📌 Project Overview

The **Rare Beauty Cosmetic Shop Ordering and Management System** is designed to computerize the daily operations of a cosmetic retail shop.

The system allows customers to browse available cosmetic products, add products to a shopping cart, place orders, and view their order history. Administrators can manage products, categories, users, inventory, and customer orders through an administrative interface.

This project was developed to apply practical knowledge of:

* Object-Oriented Programming
* Java GUI development
* Database management
* JDBC database connectivity
* Software design and development
* Client-side application development

---

## ✨ Features

### 👩‍💼 Admin Features

* Admin login
* Dashboard
* User management
* Product management
* Product category management
* Inventory management
* Order management
* Sales/order monitoring
* Add, update, delete, and view products
* Manage product stock
* View customer information

### 🛍️ Customer Features

* Customer registration
* Customer login
* Browse cosmetic products
* Browse products by category
* View product information
* Add products to shopping cart
* Update cart quantities
* Remove products from cart
* Checkout
* Place orders
* View order history
* View purchased products

---

## 🧴 Product Categories

The system supports different cosmetic product categories, including:

* Makeup Base
* Blush
* Highlighter
* Lip Products
* Eye Products
* Makeup Tools

---

## 🛠️ Technologies Used

| Technology                      | Purpose                               |
| ------------------------------- | ------------------------------------- |
| **Java**                        | Core application development          |
| **Java Swing**                  | Graphical User Interface              |
| **JFrame**                      | Desktop application windows           |
| **JDBC**                        | Database connectivity                 |
| **MySQL**                       | Database management                   |
| **MySQL Connector/J**           | Java–MySQL connectivity               |
| **Object-Oriented Programming** | Application design and implementation |

---

## 🏗️ System Architecture

The application follows a structured Java desktop application architecture where the user interface communicates with the application logic and database through JDBC.

```text
┌─────────────────────────────┐
│          User Interface     │
│       Java Swing / JFrame   │
└──────────────┬──────────────┘
               │
               ▼
┌─────────────────────────────┐
│      Application Logic      │
│     Java / OOP Components   │
└──────────────┬──────────────┘
               │
               ▼
┌─────────────────────────────┐
│       Database Layer        │
│            JDBC             │
└──────────────┬──────────────┘
               │
               ▼
┌─────────────────────────────┐
│           MySQL             │
│       rarebeauty DB         │
└─────────────────────────────┘
```

---

## 📂 Project Structure

```text
RareBeautyCosmetic/
│
├── src/
│   ├── Admin/
│   ├── Customer/
│   ├── Main/
│   └── ...
│
├── database/
│   └── rarebeauty.sql
│
├── assets/
│   └── ...
│
├── .gitignore
├── README.md
└── ...
```

> Generated files such as compiled `.class` files and IDE-specific configuration files are excluded from the repository using `.gitignore`.

---

## 🗄️ Database

The application uses **MySQL** as its database management system.

### Database Name

```text
rarebeauty
```

The database stores information related to:

* Users
* Products
* Product categories
* Inventory
* Shopping carts
* Orders
* Order details
* Customer information

The database SQL script is provided in:

```text
database/rarebeauty.sql
```

---

## ⚙️ Requirements

Before running the project, install:

* **JDK 17 or later**
* **MySQL Server**
* **MySQL Workbench** or another MySQL client
* Java-compatible IDE such as:

  * IntelliJ IDEA
  * Eclipse
  * NetBeans

You also need the **MySQL Connector/J** dependency.

---

## 🚀 Installation & Setup

### 1. Clone the Repository

```bash
git clone https://github.com/moemoeaung-dev/RareBeautyCosmetic.git
```

Move into the project directory:

```bash
cd RareBeautyCosmetic
```

---

### 2. Create the Database

Open MySQL Workbench or another MySQL client and create the database:

```sql
CREATE DATABASE rarebeauty;
```

---

### 3. Import the Database

Import:

```text
database/rarebeauty.sql
```

into the `rarebeauty` database.

---

### 4. Configure Database Connection

Update the database connection configuration with your own local MySQL credentials.

Example:

```java
String url = "jdbc:mysql://localhost:3306/rarebeauty";
String username = "root";
String password = "YOUR_PASSWORD";
```

> **Security note:** Do not commit personal database passwords or other sensitive credentials to GitHub.

---

### 5. Add MySQL Connector/J

Make sure the project has the MySQL Connector/J dependency available in the classpath.

If using an IDE, add the MySQL Connector/J library through the project's dependency/library settings.

---

### 6. Run the Application

Open the project in your preferred Java IDE and run the application's main class.

The application will start with the appropriate login interface.

---

## 🔐 User Roles

The system provides two main user roles.

### Administrator

Administrators can:

```text
Login
  ↓
Admin Dashboard
  ↓
Manage Users
Manage Products
Manage Categories
Manage Inventory
Manage Orders
View Sales/Orders
```

### Customer

Customers can:

```text
Register / Login
       ↓
Browse Products
       ↓
Add Products to Cart
       ↓
Review Cart
       ↓
Checkout
       ↓
Place Order
       ↓
View Order History
```

---

## 🎯 Project Objectives

The main objectives of this project were to:

1. Develop a desktop-based cosmetic shop management system.
2. Apply Object-Oriented Programming concepts in a practical project.
3. Develop a user-friendly graphical interface using Java Swing.
4. Implement database connectivity using JDBC.
5. Manage products and inventory efficiently.
6. Implement customer ordering functionality.
7. Provide administrative management functionality.
8. Gain practical experience in software development and database integration.

---

## 🧠 Key Concepts Applied

During the development of this project, the following concepts were applied:

* Object-Oriented Programming
* Classes and Objects
* Encapsulation
* Inheritance
* Polymorphism
* Abstraction
* Exception Handling
* Event-Driven Programming
* GUI Development
* Database Connectivity
* CRUD Operations
* Relational Database Design
* SQL Queries
* User Authentication
* Shopping Cart Management
* Order Processing

---

## 📸 Screenshots

Screenshots of the application can be added here to demonstrate the main interfaces.

### Login

*Add login page screenshot here.*

### Customer Dashboard

*Add customer dashboard screenshot here.*

### Product Management

*Add admin product management screenshot here.*

### Shopping Cart

*Add shopping cart screenshot here.*

### Order Management

*Add order management screenshot here.*

---

## 🔮 Future Improvements

Possible future improvements include:

* Online payment integration
* Product search and advanced filtering
* Product reviews and ratings
* Email notifications
* Sales analytics and graphical reports
* Improved inventory alerts
* Responsive web/mobile version
* REST API integration
* Cloud database support
* Role-based access control improvements

---

## 👩‍💻 Developer

**Moe Moe Aung**

Computer Science Student
Specialization: Artificial Intelligence

This project was developed as part of my **fourth-year university studies** to gain practical experience in Java application development, database management, GUI design, and software engineering.

---

## 📚 Academic Project

**Project:** Rare Beauty Cosmetic Shop Ordering and Management System
**Type:** Fourth-Year University Project
**Application:** Java Desktop Application
**Database:** MySQL

---

## 📄 License

This project was developed for **educational and academic purposes**.

You are welcome to explore the source code for learning and reference.
