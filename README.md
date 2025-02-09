# Real Estate Management System

## Project Overview and Purpose

The **Real Estate Management System** is a desktop application designed to facilitate the management of property listings, client interactions (buying/renting), and property tracking. It aims to assist real estate agents and property managers in managing properties and clients effectively, using a clean **Model-View-Controller (MVC)** architecture in Java.

---

## Functional Requirements

### 1. User Registration and Authentication
- **Admins** can create accounts by providing personal and professional details.
- **Admins** can add **agents** to the system.
- The system ensures secure login via **email/username** and **password**.
- **Admins** can reset passwords using the "Forgot Password" feature.

### 2. Property Listing and Management
- Agents can list properties with detailed descriptions (title, location, features, and pricing).
- Agents can upload property photos.
- The system allows agents to update or remove properties.
- Properties have status indicators: "Available," "Sold," or "Rented."

### 3. Search and Filter Options for Properties
- Users can search for properties using keywords or criteria (e.g., location, price range, property type).
- Filters are available to sort by price, size, type, and availability status.
- Search results show key property details and thumbnails.

### 4. Client Management
- Agents can register and manage client information, including contact details and property preferences.
- The system tracks client interactions (inquiries, calls, follow-ups).
- Agents can log and manage offers or agreements for rent/purchase.
- The system supports categorizing clients by activity or property preferences.

---

## Installation and Setup

### Prerequisites

To run the application locally, you need the following tools installed:

1. **WAMP Server**: For running Apache, PHP, and MySQL.  
   Download from: [WAMP Server Official Website](http://www.wampserver.com/en/)

2. **MySQL**: Comes as part of the WAMP installation to store the database.

3. **PHPMyAdmin**: For easy management of the MySQL database.

4. **JavaFX**: For the GUI component of the application.

5. **IDE (e.g., IntelliJ IDEA or Eclipse)**: To open and run the Java project.

### Steps to Set Up the Application

1. **Install WAMP Server**  
   Install WAMP Server and make sure it is running with MySQL and Apache services enabled.

2. **Clone the Repository**  
   Clone the project repository to your local machine:
   ```bash
   git clone https://github.com/Nora-Sarrawi/Real-estate-management.git
   ```

3. **Import the Database**  
   - Open PHPMyAdmin by navigating to `http://localhost/phpmyadmin` in your browser.
   - Create a new database (e.g., `real_estate`).
   - Import the `real_estate.sql` file from the `database` folder of the project into this database.

4. **Configure Database Connection**  
   - Open `src/main/resources/hibernate.cfg.xml` in the project directory.
   - Modify the `hibernate.connection.url`, `hibernate.connection.username`, and `hibernate.connection.password` to match your local MySQL setup:
   ```xml
   <hibernate-configuration>
       <session-factory>
           <!-- Database connection settings -->
           <property name="hibernate.connection.driver_class">com.mysql.cj.jdbc.Driver</property>
           <property name="hibernate.connection.url">jdbc:mysql://localhost:3306/LoboDB</property>
           <property name="hibernate.connection.username">root</property>
           <property name="hibernate.connection.password"></property>

           <!-- SQL dialect -->
           <property name="hibernate.dialect">org.hibernate.dialect.MySQL8Dialect</property>

           <!-- Enable Hibernate's automatic session context management -->
           <property name="hibernate.current_session_context_class">thread</property>

           <!-- Validate or update the database schema on startup -->
           <property name="hibernate.hbm2ddl.auto">update</property>

           <!-- Echo all executed SQL to stdout -->
           <property name="show_sql">true</property>
           <property name="format_sql">true</property>
       </session-factory>
   </hibernate-configuration>
   ```

5. **Run the Application**  
   - Open the project in your preferred IDE (e.g., IntelliJ IDEA).
   - Run the JavaFX application class to start the system.
   - The interface should launch, allowing admins to add agents, list properties, manage clients, and interact with the system.

---

## Project Architecture (MVC)

This project follows the **Model-View-Controller (MVC)** design pattern, which separates concerns into three main components:

### 1. **Model**
   - **Entities**: Represents the core data, such as properties, agents, admins, and clients.
   - **Database Interaction**: Uses Hibernate ORM for managing the interaction with the MySQL database (via `hibernate.cfg.xml` configuration).
   
### 2. **View**
   - **JavaFX UI**: The frontend user interface (UI) built using JavaFX. It includes forms for agent management, property listing, client management, and searching/filtering.
   
### 3. **Controller**
   - **Business Logic**: Contains the logic for handling user actions such as agent creation, property listing, searching, and client management. It coordinates between the View and Model.

---

## Features

- **Admin Management**: Admins can create agent accounts, manage agent profiles, and reset passwords.
- **User Authentication**: Secure agent login and account management.
- **Property Management**: Add, update, delete properties with images and status.
- **Search Functionality**: Search and filter properties by various criteria.
- **Client Management**: Track client information and interactions.
- **Property Status Tracking**: Easily view property availability status.

---

## Technologies Used

- **JavaFX**: Used for building the graphical user interface (GUI).
- **Hibernate ORM**: Used to map Java objects to database tables and simplify database interactions.
- **MySQL**: For storing property listings, client information, and transactions.
- **WAMP Server**: For running Apache, PHP, and MySQL on a local server environment.
- **JDBC**: For establishing the connection between Java and MySQL.

