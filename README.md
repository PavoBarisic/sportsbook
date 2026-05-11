# Sportsbook

Full-stack sports betting application built with Java Spring Boot and Vue.js.

![Build](https://github.com/PavoBarisic/sportsbook/actions/workflows/build.yml/badge.svg)

## About

Sportsbook is a full-stack web application that simulates a sports betting platform. Users can browse sporting events with odds, place tickets, and track their balance. Administrators can manage events, set results, and oversee all tickets and users.

## Tech Stack

**Backend**
- Java 21
- Spring Boot 3.5
- Spring Security + JWT
- Spring Data JPA + Hibernate
- PostgreSQL
- Docker & Docker Compose
- Swagger / OpenAPI
- Maven
- GitHub Actions (CI/CD)

**Frontend**
- Vue.js 3 (Composition API)
- Vite
- Pinia (state management)
- Vue Router 4
- Axios

## Features

**User**
- Register and login with JWT authentication
- Browse sporting events filtered by sport and status
- Place multi-event tickets with automatic odds calculation
- Track active, won, and lost tickets
- View account balance and profile

**Admin**
- Create, edit, and delete sporting events
- Set event results (automatically resolves all active tickets)
- View all tickets from all users
- View all registered users

## Project Structure
sportsbook/
├── .github/
│   └── workflows/
│       └── build.yml        ← CI/CD pipeline
├── src/                     ← Spring Boot backend
│   └── main/java/com/pavobarisic/sportsbook/
│       ├── controller/
│       ├── service/
│       ├── repository/
│       ├── model/
│       ├── dto/
│       ├── security/
│       └── exception/
├── frontend/                ← Vue.js frontend
│   └── src/
│       ├── views/
│       ├── components/
│       ├── stores/
│       ├── services/
│       └── router/
├── screenshots/
├── docker-compose.yml
└── README.md

## Getting Started

### Prerequisites

- Java 21
- Maven
- Docker & Docker Compose
- Node.js 18+
- npm

### 1. Clone the repository

```bash
git clone https://github.com/PavoBarisic/sportsbook.git
cd sportsbook
```

### 2. Start PostgreSQL with Docker

```bash
docker-compose up -d
```

This starts a PostgreSQL 16 instance on port 5433.

### 3. Start the backend

```bash
./mvnw spring-boot:run
```

Backend runs on `http://localhost:8082`.  
Swagger UI available at `http://localhost:8082/swagger-ui/index.html`.

### 4. Start the frontend

```bash
cd frontend
npm install
npm run dev
```

Frontend runs on `http://localhost:5173`.

### 5. Create an admin user

By default, all registered users have the USER role. To create an admin account:

1. Open the app at `http://localhost:5173` and register a new account
2. Connect to the PostgreSQL database running in Docker:

```bash
docker exec -it sportsbook-db psql -U postgres -d sportsbook
```

3. Run the following SQL query to promote your user to admin:

```sql
UPDATE korisnik SET rola = 'ADMIN' WHERE email = 'your@email.com';
```

4. Exit the database:

```sql
\q
```

5. Log out of the app and log back in — the account now has admin privileges
```sql
UPDATE korisnik SET rola = 'ADMIN' WHERE email = 'your@email.com';
```

## API Endpoints

### Auth (public)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/auth/register` | Register new user (starts with 1000.00 KM) |
| POST | `/auth/login` | Login, returns JWT token |

### Events
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/dogadaji` | Get all events (filter: ?sport= ?status=) |
| GET | `/api/dogadaji/{id}` | Get event by ID |
| POST | `/api/dogadaji` | Create event (ADMIN) |
| PUT | `/api/dogadaji/{id}` | Update event (ADMIN) |
| PUT | `/api/dogadaji/{id}/rezultat` | Set result, resolves tickets (ADMIN) |
| DELETE | `/api/dogadaji/{id}` | Delete event (ADMIN) |

### Tickets
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/tiketi` | Place ticket (USER) |
| GET | `/api/tiketi` | Get my tickets (USER) |
| GET | `/api/tiketi/{id}` | Get ticket by ID (USER) |
| GET | `/api/tiketi/sve` | Get all tickets (ADMIN) |

### Users
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/korisnici/me` | Get my profile (USER) |
| GET | `/api/korisnici` | Get all users (ADMIN) |

## How Betting Works

1. User browses upcoming events and selects odds (Home win / Draw / Away win)
2. User places a ticket with a stake amount
3. Potential winnings = stake × product of all selected odds
4. Stake is deducted from account balance immediately
5. When admin sets a result, all active tickets containing that event are resolved
6. A ticket is **won** only if all selections are correct
7. A ticket is **lost** if any single selection is wrong
8. Winnings are added to account balance automatically

## CI/CD

GitHub Actions pipeline runs on every push to master:
- Checkout code
- Set up Java 21
- Cache Maven dependencies
- Build with Maven
- Run tests against PostgreSQL service container
