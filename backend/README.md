# MetaPro Enterprises — Spring Boot MongoDB REST Backend

Production-grade Spring Boot 3 & Spring Data MongoDB backend providing high-performance REST APIs for the MetaPro Enterprises Interior Material Showroom.

## Architecture

```text
React Customer Website / Admin Panel
                 │
                 ▼
        Spring Boot REST API
                 │
                 ▼
           Service Layer
                 │
                 ▼
       Spring Data MongoDB
                 │
                 ▼
              MongoDB
```

## Features

- **Spring Web REST APIs**: Clean endpoints for products, categories, enquiries, business settings, and health checks.
- **Spring Data MongoDB**: Full document data model with automatic index creation and cascading relations.
- **Spring Security & JJWT**: Secure BCrypt password hashing and JWT token verification for protected admin endpoints.
- **Automated DataSeeder**: Seeds all 26 starter products, 8 material categories, default admin user, and business settings when the database is empty.
- **CORS Enabled**: Seamless integration with the Vite React frontend.

## REST API Endpoints

### Public (Customer Showroom)
- `GET /api/products` — Retrieve all materials (supports `category`, `availability`, `search` query parameters)
- `GET /api/products/{id}` — Retrieve material by ID or URL slug
- `GET /api/categories` — Retrieve all material categories
- `GET /api/categories/{id}` — Retrieve category by ID
- `GET /api/settings` — Retrieve business contact and showroom info
- `POST /api/enquiries` — Submit a material enquiry
- `POST /api/auth/login` — Authenticate admin credentials and issue JWT
- `GET /api/health` — Service health check

### Protected (Admin Panel — Requires Bearer JWT)
- `GET /api/auth/verify` — Validate admin session token
- `POST /api/products` — Create new material
- `PUT /api/products/{id}` — Update material specifications, prices, images
- `PATCH /api/products/{id}/availability` — Toggle between Available and Out of Stock
- `PATCH /api/products/{id}/featured` — Set product as primary homepage featured spotlight
- `DELETE /api/products/{id}` — Remove material
- `POST /api/categories` — Create category
- `PUT /api/categories/{id}` — Update category
- `DELETE /api/categories/{id}` — Delete category
- `PUT /api/settings` — Update business settings
- `GET /api/enquiries` — View contractor and project enquiry list

## Configuration

In `src/main/resources/application.yml` or via environment variables:

```bash
MONGODB_URI="mongodb://localhost:27017/metapro_db"
PORT=8080
JWT_SECRET="your-256-bit-secret"
ADMIN_INITIAL_USER="admin"
ADMIN_INITIAL_EMAIL="pdheeraj351@gmail.com"
ADMIN_INITIAL_PASSWORD="your-admin-password"
```

## Running with Maven

```bash
mvn clean package
mvn spring-boot:run
```

## Running with Docker

```bash
docker build -t metapro-backend .
docker run -p 8080:8080 -e MONGODB_URI="mongodb://host.docker.internal:27017/metapro_db" metapro-backend
```
