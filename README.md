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
