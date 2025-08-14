# Hotel Hub - Professional Setup Guide

![Quarkus](https://img.shields.io/badge/Quarkus-4695EB?style=for-the-badge&logo=quarkus&logoColor=white)
![Java](https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2CA5E0?style=for-the-badge&logo=docker&logoColor=white)

A comprehensive hotel management system built with Quarkus, featuring hotel data ingestion, search capabilities, and comprehensive API endpoints for managing hotel information, rooms, photos, facilities, and reviews.

## 📋 Table of Contents

- [Prerequisites](#prerequisites)
- [Quick Start](#quick-start)
- [Development Setup](#development-setup)
  - [IntelliJ IDEA Setup](#intellij-idea-setup)
  - [VS Code Setup](#vs-code-setup)
  - [Command Line Setup](#command-line-setup)
- [Configuration](#configuration)
- [Running the Application](#running-the-application)
- [API Documentation](#api-documentation)
- [Database Schema](#database-schema)
- [Testing](#testing)
- [Production Deployment](#production-deployment)
- [Kubernetes Deployment](#kubernetes-deployment)
- [Troubleshooting](#troubleshooting)
- [Project Architecture](#project-architecture)

## 🔧 Prerequisites

### Required Software

| Component | Version | Download Link |
|-----------|---------|---------------|
| **Java** | 17+ | [OpenJDK](https://adoptium.net/) or [Oracle JDK](https://oracle.com/java/technologies/downloads/) |
| **Maven** | 3.8+ | [Apache Maven](https://maven.apache.org/download.cgi) |
| **Docker** | 20.10+ | [Docker Desktop](https://docs.docker.com/get-docker/) |
| **Docker Compose** | 2.0+ | Included with Docker Desktop |

### Optional but Recommended

| Tool | Purpose | Download Link |
|------|---------|---------------|
| **IntelliJ IDEA** | IDE (Ultimate/Community) | [JetBrains](https://www.jetbrains.com/idea/) |
| **VS Code** | Lightweight IDE | [Microsoft](https://code.visualstudio.com/) |
| **Postman** | API Testing | [Postman](https://www.postman.com/) |

## 🚀 Quick Start

### 1. Clone the Repository

```bash
git clone <your-repository-url>
cd hotel-hub
```

### 2. Environment Variables

The project includes a `.env` file with the Cupid API key already configured:

```bash
# Check the existing environment file
cat .env

# The file contains:
CUPID_API_KEY= Add your API Key
```

**No additional setup needed** - the API key is already configured and ready to use.

### 3. Start with Docker Compose (Recommended)

```bash
# Start all services (database + application)
docker compose up -d --build

# Check service health
docker compose ps

# View logs
docker compose logs -f app
```

### 4. Verify Installation

- **Health Check**: http://localhost:8080/q/health
- **API Documentation**: http://localhost:8080/q/openapi

## 💻 Development Setup

### IntelliJ IDEA Setup

#### 1. Import Project

```bash
# Method 1: File → Open → Select hotel-hub directory
# Method 2: File → New → Project from Existing Sources
```

#### 2. Configure Project Settings

1. **Project SDK**: Go to `File → Project Structure → Project`
   - Set Project SDK to Java 17+
   - Set Project language level to 17

2. **Maven Configuration**: Go to `File → Settings → Build → Build Tools → Maven`
   - Verify Maven home directory
   - Check "Import Maven projects automatically"

3. **Annotation Processing**: Go to `File → Settings → Build → Annotation Processors`
   - Enable annotation processing
   - Set processor path to use module classpath

#### 3. Install Recommended Plugins

```bash
# Go to File → Settings → Plugins and install:
# - Quarkus Tools
# - MapStruct Support
# - Database Navigator (for PostgreSQL)
# - HTTP Client (for testing API endpoints)
```

#### 4. Configure Run Configurations

**Quarkus Dev Mode Configuration:**
1. Go to `Run → Edit Configurations`
2. Add new `Maven` configuration
3. Set:
   - Name: "Quarkus Dev"
   - Working directory: `$ProjectFileDir$`
   - Command line: `quarkus:dev`

**Docker Compose Configuration:**
1. Add new `Docker Compose` configuration
2. Set compose file: `docker-compose.yml`

#### 5. Testing HTTP Endpoints

IntelliJ includes built-in HTTP Client:
- Navigate to `http-requests/` directory
- Open any `.http` file
- Click the green arrow next to requests to execute them

### VS Code Setup

#### 1. Install Extensions

```bash
# Install these extensions:
# - Extension Pack for Java (Microsoft)
# - Quarkus (Red Hat)
# - Docker (Microsoft)
# - REST Client (Huachao Mao)
```

#### 2. Configure Settings

Create `.vscode/settings.json`:

```json
{
  "java.configuration.updateBuildConfiguration": "automatic",
  "java.server.launchMode": "Standard",
  "java.format.settings.url": "https://raw.githubusercontent.com/google/styleguide/gh-pages/eclipse-java-google-style.xml",
  "files.exclude": {
    "**/target": true
  }
}
```

#### 3. Configure Tasks

Create `.vscode/tasks.json`:

```json
{
  "version": "2.0.0",
  "tasks": [
    {
      "label": "quarkus:dev",
      "type": "shell",
      "command": "./mvnw",
      "args": ["quarkus:dev"],
      "group": "build",
      "presentation": {
        "echo": true,
        "reveal": "always",
        "focus": false,
        "panel": "shared"
      }
    }
  ]
}
```

#### 4. Testing HTTP Endpoints

Use the REST Client extension:
- Navigate to `http-requests/` directory
- Open any `.http` file
- Click "Send Request" above each HTTP request

### Command Line Setup

#### 1. Verify Prerequisites

```bash
# Check Java version
java --version

# Check Maven version
mvn --version

# Check Docker version
docker --version
docker compose version
```

#### 2. Build Project

```bash
# Clean and compile
./mvnw clean compile

# Run tests
./mvnw test

# Package application
./mvnw package -DskipTests
```

## ⚙️ Configuration

### Environment Variables

Create a `.env` file in the project root:

```env
# Required: Cupid API Key for hotel data ingestion
CUPID_API_KEY=your-api-key-here

# Optional: Database configuration (defaults work for Docker)
QUARKUS_DATASOURCE_JDBC_URL=jdbc:postgresql://localhost:5432/hotel_db
QUARKUS_DATASOURCE_USERNAME=hotel_user
QUARKUS_DATASOURCE_PASSWORD=hotel_pass

# Optional: Application configuration
QUARKUS_HTTP_PORT=8080
QUARKUS_LOG_LEVEL=INFO
```

### Application Profiles

The application supports different profiles:

#### Development Profile (`dev`)
```bash
# Start in dev mode (default)
./mvnw quarkus:dev
```

#### Production Profile (`prod`)
```bash
# Build for production
./mvnw package -Pprod

# Run production jar
java -jar target/quarkus-app/quarkus-run.jar
```

### Database Configuration

#### Local PostgreSQL Setup

If you prefer running PostgreSQL locally instead of Docker:

```bash
# Install PostgreSQL
sudo apt-get install postgresql postgresql-contrib  # Ubuntu
brew install postgresql                              # macOS

# Create database and user
sudo -u postgres psql
CREATE DATABASE hotel_db;
CREATE USER hotel_user WITH PASSWORD 'hotel_pass';
GRANT ALL PRIVILEGES ON DATABASE hotel_db TO hotel_user;
\q
```

Update `application.properties`:
```properties
quarkus.datasource.jdbc.url=jdbc:postgresql://localhost:5432/hotel_db
```

## 🏃‍♂️ Running the Application

### Method 1: Docker Compose (Recommended for Quick Start)

```bash
# Start all services
docker compose up -d --build

# Check service status
docker compose ps

# View application logs
docker compose logs -f app

# Stop all services
docker compose down

# Stop and remove volumes (clean slate)
docker compose down -v
```

### Method 2: Local Development with Docker Database

```bash
# Start only the database
docker compose up -d db

# Wait for database to be ready
docker compose logs db

# Run application locally
./mvnw quarkus:dev

# Stop database when done
docker compose stop db
```

### Method 3: Complete Local Setup

```bash
# Ensure PostgreSQL is running locally
sudo systemctl start postgresql  # Linux
brew services start postgresql   # macOS

# Start application
./mvnw quarkus:dev
```

### Method 4: Production Mode

```bash
# Build application
./mvnw package -DskipTests

# Run with Docker Compose
docker compose -f docker-compose.yml -f docker-compose.prod.yml up -d

# Or run jar directly
java -jar target/quarkus-app/quarkus-run.jar
```

## 📚 API Documentation

### Accessing API Documentation

| Service | URL | Description |
|---------|-----|-------------|
| **OpenAPI UI** | http://localhost:8080/q/openapi-ui/ | Interactive API documentation |
| **OpenAPI Spec** | http://localhost:8080/q/openapi | Raw OpenAPI specification |
| **Health Check** | http://localhost:8080/q/health | Application health status |
| **Metrics** | http://localhost:8080/q/metrics | Application metrics |

### Core API Endpoints

#### Hotel Management
- `GET /api/v1/hotels` - List hotels with filtering and pagination
- `GET /api/v1/hotels/{id}` - Get hotel details
- `GET /api/v1/hotels/{id}/rooms` - Get hotel rooms with full details
- `GET /api/v1/hotels/{id}/photos` - Get hotel photos
- `GET /api/v1/hotels/{id}/facilities` - Get hotel facilities
- `GET /api/v1/hotels/{id}/reviews` - Get hotel reviews
- `GET /api/v1/hotels/{id}/translations` - Get hotel translations
- `GET /api/v1/hotels/search` - Search hotels by name/location
- `GET /api/v1/hotels/stats` - Get hotel statistics

#### Data Ingestion
- `POST /api/v1/ingest` - Ingest hotel data from Cupid API

### Testing API Endpoints

#### Using IntelliJ HTTP Client

Navigate to `http-requests/` directory and use the provided `.http` files:

- `hotel-endpoints.http` - Basic hotel CRUD operations
- `hotel-details.http` - Detailed hotel information endpoints
- `data-ingestion.http` - Data ingestion from Cupid API
- `system-endpoints.http` - Health and metrics endpoints

#### Using cURL

```bash
# Get all hotels
curl "http://localhost:8080/api/v1/hotels"

# Get hotel by ID
curl "http://localhost:8080/api/v1/hotels/1"

# Get hotel rooms
curl "http://localhost:8080/api/v1/hotels/1/rooms"

# Search hotels
curl "http://localhost:8080/api/v1/hotels/search?q=london"

# Ingest hotel data (requires API key)
curl -X POST "http://localhost:8080/api/v1/ingest" \
  -H "Content-Type: application/json" \
  -d "[1641879, 991819, 1234567]"
```

## 🗄️ Database Schema

### Entity Relationship Diagram

The project includes comprehensive database documentation:

- **[Complete ER Diagram](ER%20diagram/hotel-er-diagram.md)** - Full database schema with relationships
- **[ER Diagram Directory](ER%20diagram/)** - Visual diagrams and technical documentation
  - `databasechangelog.png` - Visual database schema
  - `databasechangelog.drawio` - Editable diagram (Draw.io format)
  - `databasechangelog.md` - Technical schema documentation

### Database Structure

The system manages **11 entities** in a hierarchical structure:

**Main Entities:**
- `hotels` - Core hotel information
- `hotel_rooms` - Room types and details

**Hotel-Related Entities:**
- `hotel_photos` - Hotel images and media
- `hotel_facilities` - Available amenities
- `hotel_policies` - Rules and policies  
- `hotel_reviews` - Customer reviews
- `hotel_translations` - Multi-language support

**Room-Related Entities:**
- `hotel_room_bed_types` - Bed configurations
- `hotel_room_amenities` - Room-specific amenities
- `hotel_room_photos` - Room images
- `hotel_room_views` - Room views (ocean, city, etc.)

### Database Migrations

All database changes are managed through Liquibase:
- **Migration files**: `src/main/resources/db/changelog/`
- **Master changelog**: `src/main/resources/db/changelog.xml`
- **Auto-migration**: Runs automatically on application start

## 🧪 Testing

### Running Tests

```bash
# Run all tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=HotelResourceTest

# Run tests with coverage
./mvnw test jacoco:report

# Run integration tests
./mvnw verify
```

### Test Categories

- **Unit Tests**: `src/test/java/**/*Test.java`
- **Integration Tests**: `src/test/java/**/*IT.java`
- **API Tests**: Using RestAssured for endpoint testing

### Test Database (TestContainers)

The application uses **TestContainers** for isolated test execution:
- ✅ Each test class gets its own PostgreSQL container
- ✅ Tests run in isolation with fresh database state
- ✅ Automatic container cleanup after tests complete
- ✅ No manual database setup required

**Configuration:**
```properties
# Automatic test database configuration
quarkus.datasource.devservices.enabled=true
quarkus.datasource.devservices.image-name=postgres:15-alpine
quarkus.datasource.devservices.reuse=false  # Fresh container per test
```

**Requirements:**
- Docker must be running on your system
- No additional PostgreSQL installation needed

## 🚀 Production Deployment

### Docker Production Build

The project uses the root-level `Dockerfile` (not the Quarkus-generated ones in `src/main/docker/`):

```bash
# Build production image
docker build -t hotel-hub:latest .

# Run production container
docker run -p 8080:8080 \
  -e CUPID_API_KEY=your-api-key \
  -e QUARKUS_DATASOURCE_JDBC_URL=jdbc:postgresql://your-db:5432/hotel_db \
  hotel-hub:latest
```

**Note:** The `src/main/docker/` directory contains Quarkus-generated template Dockerfiles that are not actively used.

### Native Compilation (GraalVM)

```bash
# Build native executable
./mvnw package -Dnative -DskipTests

# Run native executable
./target/hotel-hub-1.0-SNAPSHOT-runner
```

### Environment-Specific Configuration

Create environment-specific configuration files:

- `application-dev.properties` - Development
- `application-staging.properties` - Staging
- `application-prod.properties` - Production

## ☸️ Kubernetes Deployment

For production Kubernetes deployments with native compilation, see our comprehensive guide:

**📖 [Complete Kubernetes Deployment Guide](k8s/KUBERNETES_DEPLOYMENT.md)**

### Quick Kubernetes Deployment

```bash
# Build native image
./mvnw package -Dnative -DskipTests

# Build Docker image  
docker build -f src/main/docker/Dockerfile.native -t hotel-hub:native .

# Deploy to Kubernetes
kubectl apply -f k8s/
```

### Native vs JVM Performance in Kubernetes

| Metric | Native | JVM | Improvement |
|--------|--------|-----|-------------|
| **Startup Time** | <1s | 5-10s | 10x faster |
| **Memory Usage** | 20-60MB | 200MB+ | 70% less |
| **Image Size** | ~50MB | ~300MB | 85% smaller |
| **Cold Start** | Instant | 5-10s | Perfect for scaling |

## 🔧 Troubleshooting

### Common Issues

#### Port Already in Use
```bash
# Error: Port 8080 is already in use
# Solution: Stop conflicting service or use different port
docker compose down
# or
./mvnw quarkus:dev -Dquarkus.http.port=8081
```

#### Database Connection Issues
```bash
# Error: Connection refused to database
# Solution: Verify database is running
docker compose ps
docker compose logs db

# Check database connection
docker compose exec db psql -U hotel_user -d hotel_db
```

#### Liquibase Migration Errors
```bash
# Error: Relation already exists
# Solution: Reset database
docker compose down -v
docker compose up -d db
```

#### MapStruct Compilation Issues
```bash
# Error: Cannot find symbol in generated mapper
# Solution: Clean and recompile
./mvnw clean compile
```

#### API Key Issues
```bash
# Error: 401 Unauthorized from Cupid API
# Solution: Verify API key is correct and properly set
echo $CUPID_API_KEY
# Ensure header is lowercase: x-api-key
```

### Logging and Debugging

#### Enable Debug Logging
```properties
# Add to application.properties
quarkus.log.category."com.hotelhub".level=DEBUG
```

#### View Application Logs
```bash
# Docker logs
docker compose logs -f app

# Local development
./mvnw quarkus:dev  # Logs appear in console
```

### Performance Tuning

#### JVM Settings
```bash
# Production JVM settings
java -XX:MaxRAMPercentage=80.0 \
     -XX:+UseG1GC \
     -XX:+UseStringDeduplication \
     -jar target/quarkus-app/quarkus-run.jar
```

#### Database Connection Pool
```properties
# Optimize database connections
quarkus.datasource.jdbc.min-size=5
quarkus.datasource.jdbc.max-size=20
quarkus.datasource.jdbc.acquisition-timeout=30s
```

## 🏗️ Project Architecture

### Technology Stack

- **Framework**: Quarkus 3.x
- **Java Version**: 17+
- **Database**: PostgreSQL 15
- **Migration**: Liquibase
- **Mapping**: MapStruct
- **Testing**: JUnit 5, RestAssured, Testcontainers
- **Documentation**: OpenAPI 3
- **Containerization**: Docker & Docker Compose

### Architecture Layers

```
┌─────────────────────────────────────────┐
│              REST Layer                 │
│   (HotelResource, IngestionResource)    │
├─────────────────────────────────────────┤
│             Service Layer               │
│    (Retrieval, Core, DataIngestion,     │
│     Search, HotelDataIngestion)         │
├─────────────────────────────────────────┤
│            Repository Layer             │
│     (Separate repository per entity)    │
│  Hotel, Photo, Room, Review, Policy,    │
│  Facility, Translation repositories     │
├─────────────────────────────────────────┤
│              Entity Layer               │
│    (Hotel + 10 related entities)        │
├─────────────────────────────────────────┤
│             Database Layer              │
│        (PostgreSQL + Liquibase)         │
└─────────────────────────────────────────┘
```

### Key Design Patterns

- **Repository Pattern**: Single Responsibility - one repository per entity
- **Service Layer**: Business logic encapsulation with specialized services
- **DTO Pattern**: Clean API contracts with MapStruct mapping
- **Separation of Concerns**: Clear boundaries between layers
- **Circuit Breaker**: External API resilience
- **TestContainers**: Isolated testing with real databases

### Project Structure

```
hotel-hub/
├── src/main/java/com/hotelhub/
│   ├── client/          # External API clients (Cupid)
│   ├── dto/             # Data Transfer Objects
│   ├── entity/          # JPA entities (11 entities)
│   ├── mapper/          # MapStruct mappers
│   ├── repository/      # Individual repositories per entity
│   ├── resource/        # REST endpoints
│   └── service/         # Specialized business services
├── src/main/resources/
│   ├── application.properties
│   └── db/              # Liquibase database migrations
├── src/test/            # Test classes with TestContainers
├── http-requests/       # HTTP test files with examples
├── ER diagram/          # Database schema documentation
│   ├── databasechangelog.png     # Visual ER diagram
│   ├── databasechangelog.drawio  # Editable diagram
│   ├── databasechangelog.md      # Schema documentation
│   └── hotel-er-diagram.md     # Complete ER diagram
├── k8s/
│   └── KUBERNETES_DEPLOYMENT.md # Kubernetes deployment guide
├── docker-compose.yml      # Container orchestration
└── README.md              # This file
```

---

## Support

For issues and questions:

1. **Check this README** first
2. **Review logs** for error messages
3. **Check existing issues** in the project repository
4. **Create a new issue** with detailed information

---

*Built with ❤️ using Quarkus - The Supersonic Subatomic Java Framework*
