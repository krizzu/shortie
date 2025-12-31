ARG FRONTEND_DIR=website
ARG API_DIR=app

# prepare api deps
FROM gradle:9-jdk21-ubi10 AS api-cache
ARG API_DIR
RUN mkdir -p /home/gradle/cache_home
RUN mkdir -p /shortie
ENV GRADLE_USER_HOME=/home/gradle/cache_home
COPY build.gradle.kts settings.gradle.kts /shortie/
COPY gradle /shortie/gradle
COPY ${API_DIR} /shortie/app
WORKDIR /shortie
RUN gradle app:dependencies --no-daemon

# build dashboard and templates
FROM node:22-alpine AS frontend-build
ARG FRONTEND_DIR
RUN mkdir /dashboard
COPY ${FRONTEND_DIR}/package.json ${FRONTEND_DIR}/.yarnrc.yml ${FRONTEND_DIR}/yarn.lock /dashboard/
WORKDIR /dashboard
RUN corepack enable
RUN yarn install --immutable

COPY ${FRONTEND_DIR} .
RUN rm -f .env # remove .env to not override envs

ENV NODE_ENV=production
RUN yarn build:dashboard
RUN yarn build:pages


# build api fatjar and copy templates
FROM gradle:9-jdk21-ubi10 AS api-build
ARG API_DIR
ARG TEMPLATE_DIR="${API_DIR}/src/main/resources/templates"
ARG ASSETS_DIR="${API_DIR}/src/main/resources/assets"

COPY --from=api-cache /home/gradle/cache_home /home/gradle/.gradle
COPY --chown=gradle:gradle . /shortie
RUN mkdir -p /shortie/${TEMPLATE_DIR}
RUN mkdir -p /shortie/${ASSETS_DIR}
COPY --from=frontend-build /dashboard/dist-pages/pages/*.html /shortie/${TEMPLATE_DIR}/
COPY --from=frontend-build /dashboard/dist-pages/assets/* /shortie/${ASSETS_DIR}/
WORKDIR /shortie
RUN gradle app:buildFatJar --no-daemon


# put everything together
FROM eclipse-temurin:21-jre AS runtime
ARG API_DIR
ARG FRONTEND_DIR
ARG APP=app

# todo: potentially wont need env.js at all if set up correctly with nginx
# install envsubst, required to subsitute envs for dashboard
RUN set -eux; \
    apt-get update; \
    apt-get install -y --no-install-recommends gettext-base; \
    cp /usr/bin/envsubst /usr/local/bin/envsubst; \
    apt-get purge -y gettext-base; \
    apt-get autoremove -y; \
    rm -rf /var/lib/apt/lists/*

RUN mkdir /${APP}
COPY --from=frontend-build /dashboard/dist /${APP}/dist
COPY ${FRONTEND_DIR}/env.template.js /${APP}/
COPY --from=api-build /shortie/${API_DIR}/build/libs/shortie.jar /${APP}/shortie.jar

COPY docker/entrypoint.sh /${APP}/
RUN chmod +x /${APP}/entrypoint.sh
WORKDIR /${APP}

ENTRYPOINT ["./entrypoint.sh"]

