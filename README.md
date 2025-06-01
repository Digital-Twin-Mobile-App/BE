
# Digital Twin Mobile App Backend

A Spring Boot application for plant monitoring and management with AI-powered plant stage detection.

## Overview

This application serves as the backend for a Digital Twin Mobile App that allows users to track and monitor plants. It uses AI to detect plant stages, species, and growth metrics from uploaded images. The system provides notifications for plant stage changes and maintains a history of plant images and their analysis.

## Features

- User authentication and authorization with JWT
- Google OAuth2 integration
- Plant management (creation, monitoring)
- Image upload and processing
- AI-powered plant analysis:
    - Plant stage detection (Germination, Vegetation, Flowering)
    - Species identification
    - Height ratio measurement
- Real-time notifications for plant stage changes
- Image history tracking
- Asynchronous processing with RabbitMQ

## Technology Stack

- **Backend**: Spring Boot 3.x
- **Database**: PostgreSQL (via Supabase)
- **Caching**: Redis
- **Message Queue**: RabbitMQ with delayed message exchange
- **Authentication**: JWT, OAuth2
- **File Storage**: Google Drive
- **Containerization**: Docker
- **AI Integration**: External AI service via REST API

## Prerequisites

- JDK 17 or higher
- Maven
- Docker and Docker Compose
- Google Drive API credentials

## Configuration

The application uses environment variables for configuration. Create a `.env` file in the root directory with the following variables:

```
# Environment
ENV=dev
DDL_UPDATE=update
AI_SERVICE_URL=http://host.docker.internal:8000/predict_file/

# Backend
BACKEND_HOST=localhost
BACKEND_PORT=8082
BACKEND_DOCKER_PORT=8080

# Drive
GOOGLE_CREDENTIALS_PATH=cred.json
GOOGLE_DRIVE_FOLDER_ID=your_folder_id

# PostgreSQL
POSTGRES_USER=your_postgres_user
POSTGRES_PASSWORD=your_postgres_password
POSTGRES_DB=postgres
POSTGRES_HOST=your_postgres_host
POSTGRES_PORT=5432

# Redis
REDIS_HOST=redis
REDIS_PORT=6379
REDIS_TIMEOUT=60000

# RabbitMQ
RABBITMQ_HOST=rabbitmq
RABBITMQ_PORT=5672

# JWT
SIGNER_KEY=your_jwt_signer_key

# Google OAuth
GOOGLE_ID=your_google_client_id
GOOGLE_PASSWORD=your_google_client_secret
GOOGLE_REDIRECT_URI=http://localhost:8082/login/oauth2/code/google

# Email Config
EMAIL=your_email@gmail.com
APP_PASSWORD=your_app_password
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_SMTP_AUTH=true
MAIL_SMTP_STARTTLS_ENABLE=true
```

## Getting Started

### Local Development

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/digital-twin-mobile-app.git
   cd digital-twin-mobile-app
   ```

2. Create a `.env` file with your configuration (see above)

3. Place your Google Drive API credentials in `cred.json`

4. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```

### Docker Deployment

1. Build and run using Docker Compose:
   ```bash
   docker-compose up -d
   ```

This will start the following services:
- Backend application
- RabbitMQ with delayed message exchange
- Redis
- RedisInsight (Redis management UI)

## API Endpoints

### Authentication
- `POST /auth/login` - User login
- `POST /auth/register` - User registration
- `POST /auth/refresh` - Refresh JWT token

### Plants
- `POST /plants/create` - Create a new plant with cover image
- `POST /plants/upload` - Upload an image for a plant
- `GET /plants/history` - Get plant image history
- `GET /plants/latest` - Get latest plant images
- `GET /plants/stats` - Get plant image statistics
- `GET /plants/my-uploads` - Get current user's plant uploads

### Users
- `GET /users/me` - Get current user information
- `PUT /users/update` - Update user information

### Notifications
- `GET /notifications` - Get user notifications

## Architecture

The application follows a microservices-inspired architecture with the following components:

1. **Core Backend** - Spring Boot application handling HTTP requests
2. **Message Queue** - RabbitMQ for asynchronous processing
3. **Cache** - Redis for caching and session management
4. **Database** - PostgreSQL for data persistence
5. **AI Service** - External service for plant analysis

## Development

### Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── project/
│   │           └── dadn/
│   │               ├── components/
│   │               │   ├── aspects/
│   │               │   ├── datainit/
│   │               │   └── rabbitmq/
│   │               │       ├── ai/
│   │               │       ├── drive/
│   │               │       └── email/
│   │               ├── configurations/
│   │               ├── controllers/
│   │               ├── dtos/
│   │               │   ├── requests/
│   │               │   └── responses/
│   │               ├── enums/
│   │               ├── exceptions/
│   │               ├── mappers/
│   │               ├── models/
│   │               ├── repositories/
│   │               ├── services/
│   │               ├── utlls/
│   │               ├── validator/
│   │               └── DadnApplication.java
│   └── resources/
│       └── application.yml
```

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Contributors

- Le Hoang Viet - 2252903

## Acknowledgments

- Spring Boot
- RabbitMQ
- Redis
- PostgreSQL
- Docker
