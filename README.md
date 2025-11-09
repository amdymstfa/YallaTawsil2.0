Voici le README mis à jour pour la **Version 2.0** avec toutes les nouvelles fonctionnalités :

```markdown
# Yallatawsil V2.0 🚚

[![Java](https://img.shields.io/badge/Java-21-blue)](https://www.java.com/) 
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.7-green)](https://spring.io/projects/spring-boot)
[![Spring AI](https://img.shields.io/badge/Spring%20AI-1.0.0--M4-orange)](https://spring.io/projects/spring-ai)
[![Maven](https://img.shields.io/badge/Maven-3.9.0-red)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/License-MIT-yellow)](LICENSE)

Backend application for **Yallatawsil**, an intelligent delivery management system with **AI-powered route optimization**, designed to help logistics companies manage deliveries, customers, vehicles, tours, and warehouses efficiently.

---

## 📖 Project Overview

Yallatawsil Backend V2 provides a comprehensive RESTful API for managing deliveries and tours with advanced route optimization powered by AI. The system helps:

- Manage customers and their delivery preferences
- Assign deliveries to customers and vehicles
- Optimize delivery routes using **3 algorithms**: Nearest Neighbor, Clarke-Wright, and **AI-powered optimization**
- Track delivery history and analyze performance patterns
- Identify problematic zones and optimize delivery schedules
- Manage vehicle capacities and availability
- Handle warehouse information and working hours

---

## 🆕 What's New in V2.0

### **New Features**
- ✨ **Customer Management** - Complete customer profile with delivery preferences
- 📊 **Delivery History** - Automatic tracking of completed deliveries with analytics
- 🤖 **AI-Powered Optimization** - Third algorithm using Spring AI (OpenAI/HuggingFace)
- 📈 **Advanced Analytics** - Delay analysis, problematic zones identification, day-of-week patterns
- 🔍 **Advanced Search** - Geographic radius search, pending deliveries, customer statistics
- 📄 **Pagination** - All list endpoints support pagination
- 🔄 **Liquibase Migrations** - Database versioning with rollback support
- 🌐 **Multi-Environment Config** - Separate profiles for DEV (H2) and QA (PostgreSQL)

### **Technical Improvements**
- ⚙️ **YAML Configuration** - Replaced `application.properties` with structured YAML
- 🏗️ **Java-based Bean Configuration** - Replaced XML with `@Configuration` classes
- 🧪 **Integration Tests** - Complete test coverage including integration scenarios
- 🐳 **Docker Support** - Containerization with Docker Compose
- 📡 **GraphQL API** (Bonus) - Alternative to REST for flexible queries
- 🌐 **ESP32 Network Monitoring** (Bonus) - IoT integration for connectivity tracking

---

## 🗂️ Core Entities

### **1. Customer** ⭐ *NEW*
- **Fields**: name, address, latitude, longitude, preferredTimeSlot
- **Relations**: Has many Deliveries, has many DeliveryHistory records
- **Features**: Geographic search, pending deliveries tracking, statistics

### **2. Delivery** 🔄 *UPDATED*
- **Fields**: weight, volume, status, notes
- **Relations**: Belongs to Customer, has DeliveryHistory
- **Status**: `PENDING`, `IN_TRANSIT`, `DELIVERED`, `FAILED`
- **Note**: Address/coordinates inherited from Customer

### **3. DeliveryHistory** ⭐ *NEW*
- **Fields**: customerName, address, deliveryDate, plannedTime, actualTime, delayMinutes, dayOfWeek
- **Purpose**: Immutable snapshot created automatically when Tour is COMPLETED
- **Analytics**: Delay patterns, problematic zones, performance metrics

### **4. Vehicle**
- **Fields**: type (TRUCK, VAN, BIKE), maxWeight, maxVolume, maxDeliveries, licensePlate
- **Types**: TRUCK (5000kg, 50m³), VAN (1500kg, 15m³), BIKE (50kg, 1m³)

### **5. Tour**
- **Fields**: date, totalDistance, estimatedDuration, optimizationAlgorithm, status
- **Status**: `PLANNED`, `IN_PROGRESS`, `COMPLETED`, `CANCELLED`
- **Feature**: Auto-creates DeliveryHistory when status → COMPLETED

### **6. Warehouse**
- **Fields**: name, address, latitude, longitude, openingTime, closingTime

---

## ⚡ Features

### **Core Features**
- ✅ Complete CRUD for Customers, Deliveries, Vehicles, Tours, Warehouses
- ✅ Route optimization with **3 algorithms**:
  1. **Nearest Neighbor** (Greedy, fast)
  2. **Clarke-Wright Savings** (Better optimization)
  3. **AI Optimizer** (Pattern learning from history) 🤖
- ✅ Automatic delivery history tracking
- ✅ Advanced analytics and reporting
- ✅ Geographic radius search
- ✅ Pagination on all list endpoints
- ✅ RESTful API documented with **Swagger/OpenAPI**

### **Analytics Features** 📊
- Average delay by day of week
- Problematic zones identification (addresses with high delays)
- Customer delivery statistics
- Historical pattern analysis for AI optimization

### **Advanced Features**
- 🔄 Database migrations with Liquibase (versioned + rollback)
- 🧪 Integration tests with JUnit 5
- 🐳 Docker containerization
- 📡 GraphQL API (alternative to REST)
- 🌐 ESP32 network monitoring (IoT integration)

---

## 🚀 Getting Started

### Prerequisites
- **Java 21+**
- **Maven 3.9+**
- **PostgreSQL** (for QA environment)
- **Docker** (optional)
- **OpenAI API Key** or **Ollama** (for AI optimizer)

### Clone & Install
```bash
git clone https://github.com/yourusername/yallatawsil-v2.git
cd yallatawsil-v2
mvn clean install
```

### Run - Development (H2 in-memory)
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Run - QA (PostgreSQL)
```bash
export DB_PASSWORD=your_password
mvn spring-boot:run -Dspring-boot.run.profiles=qa
```

### Access Swagger UI
Visit [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

### Access H2 Console (DEV only)
Visit [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
- JDBC URL: `jdbc:h2:mem:yallatawsildb`
- Username: `sa`
- Password: *(empty)*

---

## 🔧 Configuration

### Environment Profiles

#### **application-dev.yml** (Development)
```yaml
spring:
  datasource:
    url: jdbc:h2:mem:yallatawsildb
    driver-class-name: org.h2.Driver
  jpa:
    show-sql: true
  
tour:
  optimizer:
    algorithm: NEAREST_NEIGHBOR  # Fast for testing
```

#### **application-qa.yml** (QA/Testing)
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/yallatawsil_qa
    username: yalla_user
    password: ${DB_PASSWORD}
  
tour:
  optimizer:
    algorithm: CLARKE_WRIGHT  # Best performance
```

### AI Configuration

For **OpenAI**:
```yaml
spring.ai:
  openai:
    api-key: ${OPENAI_API_KEY}
    chat:
      options:
        model: gpt-4
        temperature: 0.7
```

For **Ollama** (local):
```bash
# Install Ollama
curl -fsSL https://ollama.com/install.sh | sh

# Pull TinyLlama model
ollama pull tinyllama

# Configure in application.yml
tour:
  optimizer:
    algorithm: AI_OPTIMIZER
```

---

## 🗃️ API Endpoints

### **Customers** ⭐ *NEW*
```
GET    /api/v1/customers                    - List all customers (paginated)
POST   /api/v1/customers                    - Create customer
GET    /api/v1/customers/{id}               - Get customer details
PUT    /api/v1/customers/{id}               - Update customer
DELETE /api/v1/customers/{id}               - Delete customer (CASCADE)

# Advanced Searches
GET    /api/v1/customers/search/by-name?name=Ahmed
GET    /api/v1/customers/search/by-address?address=Casablanca
GET    /api/v1/customers/search/within-radius?latitude=33.5&longitude=-7.6&radiusKm=5
GET    /api/v1/customers/with-pending-deliveries
```

### **Deliveries** 🔄 *UPDATED*
```
GET    /api/v1/deliveries                   - List all deliveries (paginated)
POST   /api/v1/deliveries                   - Create delivery (requires customerId)
GET    /api/v1/deliveries/{id}              - Get delivery details
PUT    /api/v1/deliveries/{id}              - Update delivery
DELETE /api/v1/deliveries/{id}              - Delete delivery
PATCH  /api/v1/deliveries/{id}/status       - Update status
GET    /api/v1/deliveries/status/{status}   - Filter by status
```

### **Delivery History** ⭐ *NEW*
```
GET    /api/v1/delivery-history                          - List all history (paginated)
GET    /api/v1/delivery-history/{id}                     - Get history details
GET    /api/v1/delivery-history/customer/{customerId}    - Customer's history
GET    /api/v1/delivery-history/tour/{tourId}            - Tour's history
GET    /api/v1/delivery-history/delivery/{deliveryId}    - Delivery's history

# Analytics
GET    /api/v1/delivery-history/analytics/delayed
GET    /api/v1/delivery-history/analytics/average-delay-by-day
GET    /api/v1/delivery-history/analytics/problematic-zones?threshold=15
```

### **Tours** 🔄 *UPDATED*
```
GET    /api/v1/tours                        - List all tours
POST   /api/v1/tours/optimize               - Create optimized tour
GET    /api/v1/tours/{id}                   - Get tour details
DELETE /api/v1/tours/{id}                   - Delete tour
PATCH  /api/v1/tours/{id}/status            - Update status (auto-creates history)
POST   /api/v1/tours/compare                - Compare optimization algorithms
```

### **Vehicles**
```
GET    /api/v1/vehicles                     - List vehicles
POST   /api/v1/vehicles                     - Create vehicle
GET    /api/v1/vehicles/{id}                - Get vehicle details
PUT    /api/v1/vehicles/{id}                - Update vehicle
DELETE /api/v1/vehicles/{id}                - Delete vehicle
```

### **Warehouses**
```
GET    /api/v1/warehouses                   - List warehouses
POST   /api/v1/warehouses                   - Create warehouse
GET    /api/v1/warehouses/{id}              - Get warehouse details
PUT    /api/v1/warehouses/{id}              - Update warehouse
DELETE /api/v1/warehouses/{id}              - Delete warehouse
```

---

## 📊 Database Schema

### Liquibase Migrations
```
db/changelog/
├── db.changelog-master.yaml
├── changes/
│   ├── create-vehicles-table.yaml
│   ├── create-warehouses-table.yaml
│   ├── create-deliveries-table.yaml
│   ├── create-tours-table.yaml
│   ├── create-tour-deliveries.yaml
│   ├── v2.0-create-customers-table.yaml          ⭐ NEW
│   ├── v2.0-modify-deliveries-table.yaml         ⭐ NEW
│   ├── v2.0-create-delivery-history-table.yaml   ⭐ NEW
│   └── v2.0-seed-customers-data.yaml             ⭐ NEW
```

### Rollback Example
```bash
mvn liquibase:rollback -Dliquibase.rollbackCount=1
```

---

## 🐳 Docker

### Build & Run
```bash
docker-compose up --build
```

### Docker Compose Services
- **yallatawsil-api** - Spring Boot application
- **postgres-qa** - PostgreSQL database
- **esp32-monitor** (optional) - Network monitoring service

---

## 🧪 Testing

### Unit Tests
```bash
mvn test
```

### Integration Tests
```bash
mvn verify
```

### Code Coverage (JaCoCo)
```bash
mvn clean test jacoco:report
open target/site/jacoco/index.html
```

---

## 🤖 AI Optimizer Usage

### Request Example
```json
POST /api/v1/tours/optimize
{
  "vehicleId": 1,
  "warehouseId": 1,
  "date": "2025-11-15",
  "deliveryIds": [1, 2, 3, 4, 5],
  "optimizationAlgorithm": "AI_OPTIMIZER"
}
```

### How it Works
1. **Retrieves historical data** from DeliveryHistory
2. **Analyzes patterns**: delays by day of week, problematic zones, customer preferences
3. **Sends prompt to AI** with historical context + current deliveries
4. **Receives optimized route** with recommendations
5. **Returns ordered deliveries** with predicted distance

---

## 📈 Analytics Examples

### Average Delay by Day of Week
```json
GET /api/v1/delivery-history/analytics/average-delay-by-day

Response:
{
  "MONDAY": {
    "averageDelayMinutes": 12.5,
    "totalDeliveries": 45
  },
  "FRIDAY": {
    "averageDelayMinutes": 22.3,
    "totalDeliveries": 38
  }
}
```

### Problematic Zones
```json
GET /api/v1/delivery-history/analytics/problematic-zones?threshold=15

Response:
[
  {
    "address": "123 Rue des Hospitals",
    "averageDelayMinutes": 25.7,
    "totalDeliveries": 12
  }
]
```

---

## 🏗️ Architecture

```
com.yallatawsil.backend/
├── config/              - Java-based configuration (replaces XML)
├── controller/          - REST endpoints
├── dto/                 - Request/Response DTOs
│   ├── request/
│   └── response/
├── entity/              - JPA entities
│   └── enums/
├── exception/           - Custom exceptions
├── mapper/              - MapStruct mappers (DTO ↔ Entity)
├── repository/          - Spring Data JPA repositories
├── service/             - Business logic
│   ├── InterfaceEntity/
│   ├── InterfaceEntityImpl/
│   ├── optimizer/       - Route optimization algorithms
│   └── distance/        - Distance calculation
```

---

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 📌 Contact

**Developed by Moustapha Ndiaye**

- 📧 Email: [amdymoustapha011@gmail.com](mailto:amdymoustapha011@gmail.com)
- 💼 LinkedIn: [Your LinkedIn](https://linkedin.com/in/yourprofile)
- 🐙 GitHub: [Your GitHub](https://github.com/yourusername)

---

## 🙏 Acknowledgments

- Spring Boot Team
- Spring AI Project
- OpenAI / HuggingFace
- MapStruct
- Liquibase

---

