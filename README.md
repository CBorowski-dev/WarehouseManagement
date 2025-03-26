# Warehouse Management System for Electronic Parts

A Spring Boot application for managing electronic parts inventory in a warehouse.

## Features

- **Input Form**: Add and edit electronic parts with details like type, name, properties, quantity, storage location, and optional image.
- **Search Functionality**: Search for parts by type, name, properties, and storage location.
- **Detail View**: View and edit detailed information about each electronic part.
- **Delete Functionality**: Remove electronic parts from the inventory with confirmation.

## Technologies Used

- **Backend**: Spring Boot 3.4.4, Spring Data JPA
- **Frontend**: Thymeleaf, Bootstrap 5
- **Database**: PostgreSQL
- **Build Tool**: Maven

## Prerequisites

- Java 17 or higher
- PostgreSQL database
- Maven

## Setup and Installation

1. **Clone the repository**

```bash
git clone https://github.com/yourusername/warehouse-management.git
cd warehouse-management
```

2. **Configure the database**

Create a PostgreSQL database named `warehouse_db`:

```sql
CREATE DATABASE warehouse_db;
```

Update the database configuration in `src/main/resources/application.properties` if needed:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/warehouse_db
spring.datasource.username=postgres
spring.datasource.password=postgres
```

3. **Build and run the application**

```bash
./mvnw spring-boot:run
```

4. **Access the application**

Open your browser and navigate to:

```
http://localhost:8080
```

## Database Schema

The application uses the following database tables:

- **electronic_parts**: Stores information about electronic parts
- **part_types**: Stores different types of electronic parts
- **storage_locations**: Stores different storage locations

## Default Data

The application initializes the database with default part types and storage locations on first run:

- **Part Types**: Capacitor, Power Supply, Resistor, Microcontroller, Transistor, Diode, LED, Sensor, Switch, Connector
- **Storage Locations**: Carton 1, Carton 2, Shelf Top, Shelf Middle, Shelf Bottom, Drawer Top, Drawer Bottom, Cabinet A, Cabinet B, Workbench

## License

This project is licensed under the MIT License - see the LICENSE file for details.
