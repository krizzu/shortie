<p align="center">
    <img height="100" src="assets/shortie-logo.png" alt="shortie-logo" align="center" />
</p>

<p align="center">
  <b>A minimal, self-hostable URL shortening service with an admin dashboard for managing links.</b><br>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Kotlin-2.2.20-7F52FF?logo=kotlin&logoColor=white" alt="Kotlin 2.2.20" />
  <img src="https://img.shields.io/badge/Ktor-3.3.2-0095D5?logo=ktor&logoColor=white" alt="Ktor 3.3.2" />
  <img src="https://img.shields.io/badge/Coroutines-1.10.2-0095D5" alt="Kotlin Coroutines 1.10.2" />
  <img src="https://img.shields.io/badge/Koin-4.1.1-FE7A16" alt="Koin 4.1.1" />
  <br/>
  <img src="https://img.shields.io/badge/PostgreSQL-42.7.3-336791?logo=postgresql&logoColor=white" alt="PostgreSQL 42.7.3" />
  <img src="https://img.shields.io/badge/Exposed-1.0.0--rc--4-000000" alt="Exposed 1.0.0-rc-4" />
  <img src="https://img.shields.io/badge/HikariCP-7.0.2-2C2C2C" alt="HikariCP 7.0.2" />
  <img src="https://img.shields.io/badge/Flyway-10.22.0-CC0200?logo=flyway&logoColor=white" alt="Flyway 10.22.0" />
  <br/>
  <img src="https://img.shields.io/badge/Docker-Friendly-2496ED?logo=docker&logoColor=white" alt="Docker Friendly" />
  <img src="https://img.shields.io/badge/License-Apache_2.0-blue.svg" alt="License: Apache 2.0" />
</p>

## Features

- Short-code based URLs for compact links
- Custom aliases for human-readable links
- Expiring links with expiration dates
- Password protected links
- Modern admin dashboard with a easy UI
- Docker friendly set up

## Getting started

Copy content from `.env-example` file into `.env`.
Run docker via `docker compose up -d`.

Example `compose.yaml` file:

```yaml
services:
  db:
    image: postgres:18.1
    container_name: db
    restart: unless-stopped
    volumes:
      - postgres_data:/var/lib/postgresql
    environment:
      POSTGRES_DB: ${APP_DB_NAME}
      POSTGRES_USER: ${APP_DB_USER}
      POSTGRES_PASSWORD: ${APP_DB_PASSWORD}
    healthcheck:
      test: [ "CMD-SHELL", "pg_isready -U ${APP_DB_USER} -d ${APP_DB_NAME}" ]
      interval: 1s

  shortie:
    image: krizzu/shortie:dev # or other tag
    container_name: app
    restart: unless-stopped
    ports:
      - "80:80" # api port
      - "81:81" # dashboard port
    depends_on:
      - db
    environment:
      APP_DB_URL: jdbc:postgresql://db:5432/${APP_DB_NAME}
      APP_DB_PASSWORD: ${APP_DB_PASSWORD}
      APP_DB_USER: ${APP_DB_USER}
      APP_ID_ALPHABET: ${APP_ID_ALPHABET}
      APP_AUTH_SECRET: ${APP_AUTH_SECRET}
      APP_ADMIN_PASSWORD: ${APP_ADMIN_PASSWORD}

      # keep this in sync with exposed api port on host for proper redirections
      # defaults to 80
      #APP_API_PROXY_PORT: 4000

volumes:
  postgres_data:
```

## Tech stack

- Kotlin + Ktor → HTTP server and routing
- Exposed + HikariCP + Flyway → Database access, pooling and migrations
- Koin → Dependency injection
- TypeScript + React → Frontend
- Tanstack Router → Frontend router
- Tailwind CSS → Styling


## Local development

See [DEVELOPMENT.md](./DEVELOPMENT.md) guide.

## License

```
Copyright 2026 Krzysztof Borowy

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
