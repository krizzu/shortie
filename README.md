# shortie

A link shortening application build with Ktor.

## Tech stack

### Libraries

- Ktor
- Exposed + Hikari
- Flyway
- Koin

### Architecture

todo

## Local development

### Prepare .env file

Copy content from .env-example into .env and edit accordingly

### Running migrations

- Source .env file

```shell
set -a; source .env; set +a
```

- Run migrations script. Pass in a migration name like "V<version>__<description>"

```shell
./gradlew :app:generateMigrations -PmigrationName="V1__initial"
```

## License

todo
