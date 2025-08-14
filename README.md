# Hotel Hub API

![Java](https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![Quarkus](https://img.shields.io/badge/Quarkus-4695EB?style=for-the-badge&logo=quarkus&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2CA5E0?style=for-the-badge&logo=docker&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-yellow.svg?style=for-the-badge)

Hotel Hub is a comprehensive, high-performance hotel management API built with Quarkus. It provides a robust platform for ingesting, searching, and managing hotel data, including rooms, photos, facilities, and reviews.

## ✨ Features

- **Data Ingestion**: Seamlessly ingest hotel data from external APIs.
- **Advanced Search**: Powerful search capabilities to find hotels by name, location, and more.
- **Comprehensive API**: A rich set of endpoints for managing all aspects of hotel information.
- **Detailed Data Model**: A complete relational database schema to manage hotel details.
- **Containerized**: Ready to run with Docker and Docker Compose.
- **Cloud-Native**: Optimized for performance and scalability with Quarkus and GraalVM.

## 🚀 Quick Start

Get the application running in just a few steps:

1.  **Clone the repository:**
    ```bash
    git clone <your-repository-url>
    cd hotel-hub
    ```

2.  **Set up your environment file:**
    Rename `.env.example` to `.env` and add your Cupid API key:
    ```bash
    mv .env.example .env
    # Now edit .env and add your key
    ```

3.  **Run with Docker Compose:**
    ```bash
    docker compose up -d --build
    ```

The application will be available at `http://localhost:8080`.

## 🗄️ Database Schema

The database schema is designed to be comprehensive and relational. Here is a visual representation of the tables and their relationships:

![ER Diagram](ER%20diagram/databasechangelog.png)

For more details, see the [full ER diagram documentation](ER%20diagram/hotel-er-diagram.md).

## 📚 API Documentation

The API is documented using the OpenAPI standard. Once the application is running, you can access the interactive API documentation at:

- **OpenAPI UI**: `http://localhost:8080/q/openapi-ui/`

### Core Endpoints

| Method | Endpoint                               | Description                                |
| :----- | :------------------------------------- | :----------------------------------------- |
| `GET`  | `/api/v1/hotels`                       | List hotels with filtering and pagination  |
| `GET`  | `/api/v1/hotels/{id}`                  | Get hotel details                          |
| `GET`  | `/api/v1/hotels/{id}/rooms`            | Get hotel rooms with full details          |
| `GET`  | `/api/v1/hotels/{id}/photos`           | Get hotel photos                           |
| `GET`  | `/api/v1/hotels/{id}/facilities`       | Get hotel facilities                       |
| `GET`  | `/api/v1/hotels/{id}/reviews`          | Get hotel reviews                          |
| `GET`  | `/api/v1/hotels/{id}/translations`     | Get hotel translations                     |
| `GET`  | `/api/v1/hotels/search`                | Search hotels by name/location             |
| `GET`  | `/api/v1/hotels/stats`                 | Get hotel statistics                       |
| `POST` | `/api/v1/ingest`                       | Ingest hotel data from the Cupid API       |

## 🔧 Development

For detailed instructions on setting up a development environment, running tests, and deploying to production, please refer to the original [SETUP_GUIDE.md](SETUP_GUIDE.md).
