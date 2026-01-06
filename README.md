# shortie

A link shortening app build with Ktor.

## Tech stack

## Requirements

- JDK 21
- Node 22+

### Libraries

- Ktor
- Exposed + Hikari
- Flyway
- Koin
- Vite + Tailwind (Admin dashboard + pages)

### Architecture

todo

## Local development

### Prepare .env file

Copy content from .env-example into .env and edit accordingly

### Creating migrations

Perform required database changes, then follow steps:

- Source .env file

```shell
set -a; source .env; set +a
```

- Run migrations script. Pass in a migration name like "V<version>__<description>"

```shell
./gradlew :api:generateMigrations -PmigrationName="V1__initial"
```

### Build static content

Static content is generated from `frontend` project. Node 22+ is required to run it.

- install dependencies in `frontend` via `yarn install`
- run `./scripts/build-pages.sh` to build and copy assets into proper directories

## License

todo
