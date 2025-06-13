# URL Shortener Service

A simple URL shortening  built with Spring Boot.  
It supports creating short URLs with expiration, caching original URLs in Redis, tracking visits, and deleting entries.

---

## Features

- ✅ Shorten URLs with expiration (TTL)
- ⚡ Redis caching for quick redirects
- 📈 Track access count (DB + Redis)
- 🧼 URL format validation
- 🔐 Secure and fast
- 🧹 Delete short URLs

---

## Example: application.properties
- spring.application.name=url-shortener
- server.port=8080
- spring.datasource.url=jdbc:postgresql://localhost:5432/url_shortener
- spring.jpa.properties.hibernate.dialect = org.hibernate.dialect.PostgreSQLDialect
-  spring.datasource.driver-class-name=org.postgresql.Driver
-  spring.datasource.hikari.connectionTimeout=20000
-  spring.datasource.hikari.maximumPoolSize=5
- spring.jpa.database=POSTGRESQL
- spring.datasource.username=postgres
- spring.datasource.password=postgres
- spring.jpa.hibernate.ddl-auto=update
- spring.jpa.show-sql=true
- spring.data.redis.host=localhost
- spring.data.redis.port=6379

# Redis config
- spring.data.redis.host=localhost
- spring.data.redis.port=6379

---

## Getting Started
## Create
 POST http://localhost:8080/api/v1/url-shortener \
request body: {
 "originalUrl": "http://hamropatro.com",
 "expireInMinutes": 5
 }

## Get Original Url by short code
GET http://localhost:8080/api/v1/url-shortener/get-originalurl/{shortUrl}

# Get all
GET http://localhost:8080/api/v1/url-shortener

# Get by ID
GET http://localhost:8080/api/v1/url-shortener/get-by-id/{uuid}

# Delete
DELETE http://localhost:8080/api/v1/url-shortener/{uuid}


---

### Prerequisites

- Java 21
- Maven 3.x
- PostgreSQL database
- Redis server

### Build & Run

```bash
git clone https://github.com/BibekRegmi9/url-shortener.git
cd url-shortener

