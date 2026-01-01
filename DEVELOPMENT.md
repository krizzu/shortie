# Local development

## Running build:

- Run compose-dev to start database and nginx server:

```shell
docker compose -f compose-dev.yml up -d
```

- Run dashboard in dev mode:

```shell
cd website && yarn dev
```

- Run api app:

todo

## Docker

Building image:

```shell
docker build . -f ./docker/Dockerfile -t krizzu/shortie:latest
```
