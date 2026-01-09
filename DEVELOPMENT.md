# Local development

## Requirements

- JDK 21
- Node 22+

## Running locally

In order to run the app locally, follow these steps:

- set up `.env` file with content from `.env-example`
- run dev containers
- run frontend
- run api

### Run dev containers

Run docker compose:

```shell
docker compose -f compose-dev.yml up -d
```

### Run frontend

Inside `frontend` directory:

- install dependencies with `yarn`
- run vite project via `yarn dev` (make sure it runs at port 5137)

### Running api

Having your shell sourced with `.env` files, either run Gradle task

```shell
./gradlew api:runFatJar
```

or [see the Run a Ktor application section in the IntelliJ IDEA documentation](https://www.jetbrains.com/help/idea/ktor.html#run_ktor_app).
Make sure the run configuration has ENV variables set up correctly (from `.env` file).

## Build and deploy dev builds

`deploy-docker-dev` script builds `dev` tags and publish it to docker hub.
It creates tags:
- krizzu/shortie:X.Y.Z-dev
- krizzu/shortie:dev

Run it, passing in semantic version:

```shell
./scripts/deploy-docker-dev.sh 1.2.3
```

## Creating migrations

Perform required database changes, then follow steps:

- Source .env file

```shell
set -a; source .env; set +a
```

- Run migrations script. Pass in a migration name like "V<version>__<description>"

```shell
./gradlew :api:generateMigrations -PmigrationName="V1__initial"
```

## Build static content

Static content is generated from `frontend` project. It's then server from API as templates.
Each time you build the pages, you have to reset local API.

- install dependencies in `frontend` via `yarn`
- run `./scripts/build-pages.sh` in root to build and copy assets into API project
