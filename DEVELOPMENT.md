# Local development

## Running locally

Local development give you access to dashboard at port 4001, api at 4000.

To run it locally, you have to:

- set up env variable in root (copy from .env-example)
- run dev containers (nginx and db)
- run frontend
- run api

### Running dev containers

Simply run docker compose:

```shell
docker compose -f compose-dev.yml up -d
```

### Running frontend

Inside `frontend` directory:

- install dependencies with `yarn`
- run vite project via `yarn dev` (make sure it runs at port 5137)

### Running api

todo

## Build and deploy dev builds

`deploy-docker-dev` script builds and deploys `dev` tags of the app.

Run it, passing in semantic version:

```shell
./scripts/deploy-docker-dev.sh 1.2.3
```


