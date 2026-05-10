README.md — kreiraj u root folderu projekta:
markdown

# Sportsbook API

REST API za sportsko klađenje izrađen u Java Spring Boot-u.

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

## Pokretanje

### 1. Pokreni bazu podataka

```bash
docker compose up -d
```

### 2. Pokreni aplikaciju

Pokreni kroz IntelliJ IDEA ili:

```bash
./mvnw spring-boot:run
```

### 3. Swagger UI

http://localhost:8082/swagger-ui/index.html


## Autentikacija

API koristi JWT token autentikaciju.

1. Registriraj se na `POST /auth/register`
2. Prijavi se na `POST /auth/login`
3. Kopiraj token iz odgovora
4. Klikni **Authorize** u Swagger UI i unesi token

## Role

| Rola | Opis |
|------|------|
| USER | Pregled događaja, postavljanje tiketa, pregled vlastitih tiketa |
| ADMIN | Sve što USER može + upravljanje događajima, postavljanje rezultata, pregled svih tiketa |

> Da bi korisnik bio ADMIN, potrebno je ručno promijeniti rolu u bazi:
> ```sql
> UPDATE korisnik SET rola = 'ADMIN' WHERE email = 'tvoj@email.com';
> ```

## Endpointi

### Auth
| Metoda | Putanja | Pristup | Opis |
|--------|---------|---------|------|
| POST | /auth/register | Javno | Registracija (dobiva 1000.00 KM) |
| POST | /auth/login | Javno | Login, vraća JWT token |

### Događaji
| Metoda | Putanja | Pristup | Opis |
|--------|---------|---------|------|
| GET | /api/dogadaji | USER, ADMIN | Svi događaji (filter: ?sport=, ?status=) |
| GET | /api/dogadaji/{id} | USER, ADMIN | Jedan događaj |
| POST | /api/dogadaji | ADMIN | Kreiraj događaj |
| PUT | /api/dogadaji/{id} | ADMIN | Ažuriraj događaj |
| PUT | /api/dogadaji/{id}/rezultat | ADMIN | Postavi rezultat i razriješi tikete |
| DELETE | /api/dogadaji/{id} | ADMIN | Obriši događaj |

### Tiketi
| Metoda | Putanja | Pristup | Opis |
|--------|---------|---------|------|
| POST | /api/tiketi | USER, ADMIN | Postavi tiket |
| GET | /api/tiketi | USER, ADMIN | Moji tiketi |
| GET | /api/tiketi/{id} | USER, ADMIN | Jedan tiket |
| GET | /api/tiketi/sve | ADMIN | Svi tiketi |

### Korisnici
| Metoda | Putanja | Pristup | Opis |
|--------|---------|---------|------|
| GET | /api/korisnici/me | USER, ADMIN | Moj profil i stanje računa |
| GET | /api/korisnici | ADMIN | Svi korisnici |

## Primjer postavljanja tiketa

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

PostgreSQL baza radi na portu `5433`, aplikacija na portu `8082`.