# Sportsbook API
![Build](https://github.com/PavoBarisic/sportsbook/actions/workflows/build.yml/badge.svg)
REST API for sports betting built with Java Spring Boot.

## Tech Stack

- Java 21
- Spring Boot 3.5.14
- Spring Security + JWT
- Spring Data JPA + Hibernate
- PostgreSQL
- Docker
- Swagger / OpenAPI
- Lombok
- Maven

## Getting Started

### 1. Start the database

```bash
docker compose up -d
```

### 2. Run the application

Run through IntelliJ IDEA or:

```bash
./mvnw spring-boot:run
```

### 3. Swagger UI
http://localhost:8082/swagger-ui/index.html

## Authentication

The API uses JWT token authentication.

1. Register at `POST /auth/register`
2. Login at `POST /auth/login`
3. Copy the token from the response
4. Click **Authorize** in Swagger UI and enter the token

## Roles

| Role | Description |
|------|-------------|
| USER | View events, place tickets, view own tickets |
| ADMIN | Everything USER can do + manage events, set results, view all tickets |

> To make a user ADMIN, manually update the role in the database:
> ```sql
> UPDATE korisnik SET rola = 'ADMIN' WHERE email = 'your@email.com';
> ```

## Endpoints

### Auth
| Method | Path | Access | Description |
|--------|------|--------|-------------|
| POST | /auth/register | Public | Register (receives 1000.00 on account) |
| POST | /auth/login | Public | Login, returns JWT token |

### Events
| Method | Path | Access | Description |
|--------|------|--------|-------------|
| GET | /api/dogadaji | USER, ADMIN | All events (filter: ?sport=, ?status=) |
| GET | /api/dogadaji/{id} | USER, ADMIN | Single event |
| POST | /api/dogadaji | ADMIN | Create event |
| PUT | /api/dogadaji/{id} | ADMIN | Update event |
| PUT | /api/dogadaji/{id}/rezultat | ADMIN | Set result and resolve tickets |
| DELETE | /api/dogadaji/{id} | ADMIN | Delete event |

### Tickets
| Method | Path | Access | Description |
|--------|------|--------|-------------|
| POST | /api/tiketi | USER, ADMIN | Place a ticket |
| GET | /api/tiketi | USER, ADMIN | My tickets |
| GET | /api/tiketi/{id} | USER, ADMIN | Single ticket |
| GET | /api/tiketi/sve | ADMIN | All tickets |

### Users
| Method | Path | Access | Description |
|--------|------|--------|-------------|
| GET | /api/korisnici/me | USER, ADMIN | My profile and account balance |
| GET | /api/korisnici | ADMIN | All users |

## Ticket Example

```json
POST /api/tiketi
{
    "ulog": 10.00,
    "stavke": [
        {"dogadajId": 1, "tip": "DOMACIN"},
        {"dogadajId": 2, "tip": "GOST"}
    ]
}
```

## Docker

PostgreSQL runs on port `5433`, application runs on port `8082`.