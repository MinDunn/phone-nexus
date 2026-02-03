# PhoneNexus 📱

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-18-blue)](https://reactjs.org/)
[![Flutter](https://img.shields.io/badge/Flutter-3.x-cyan)](https://flutter.dev/)
[![Docker](https://img.shields.io/badge/Docker-Enabled-blue)](https://www.docker.com/)

**PhoneNexus** is a modern, high-end e-commerce ecosystem specialized in mobile devices. Built with a scalable microservices architecture, it provides a seamless experience for customers across Web and Mobile platforms, while offering powerful management tools for administrators.

## 🏗️ Architecture

PhoneNexus follows a **Microservices Architecture** to ensure high availability, scalability, and independent deployment.

- **Gateway**: Single entry point using Spring Cloud Gateway.
- **Identities**: Authentication and Authorization service using JWT & Spring Security.
- **Products**: Management of phone catalogs, categories, and dynamic attributes.
- **Sales**: Order processing, cart management, and transaction history.
- **Frontend**: Responsive React web application for customers and admins.
- **Mobile**: Cross-platform Flutter app for on-the-go shopping.

## 🛠️ Tech Stack

### Backend
- **Core**: Java 25, Spring Boot 3
- **Microservices**: Spring Cloud Gateway, Netflix Eureka (Discovery)
- **Security**: Spring Security, JWT
- **Persistence**: Hibernate/JPA, PostgreSQL
- **Docs**: Swagger UI / OpenAPI 3

### Frontend
- **Web**: ReactJS, Redux Toolkit, Tailwind CSS, Axios
- **Mobile**: Flutter, Riverpod, Dio

### Infrastructure
- **Containerization**: Docker, Docker Compose
- **Cloud Services**: Firebase (Auth, FCM, Storage)

## 🚀 Getting Started

### Prerequisites
- Docker & Docker Compose
- Java 25 (for local development)
- Node.js & npm
- Flutter SDK

### Run with Docker
```bash
# Clone the repository
git clone <your-repo-url>
cd phone-nexus

# Start infrastructure (databases)
docker-compose up -d
```

## 👤 Author
- **Name**: MinDunn
- **GitHub**: [@MinDunn](https://github.com/MinDunn)

## 📄 License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
