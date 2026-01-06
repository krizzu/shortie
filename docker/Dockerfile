ARG FRONTEND_DIR=frontend
ARG API_DIR=api

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
COPY --chown=gradle:gradle . /shortie
RUN mkdir -p /shortie/${TEMPLATE_DIR}
RUN mkdir -p /shortie/${ASSETS_DIR}
COPY --from=frontend-build /dashboard/dist-pages/pages/*.html /shortie/${TEMPLATE_DIR}/
COPY --from=frontend-build /dashboard/dist-pages/assets/* /shortie/${ASSETS_DIR}/
WORKDIR /shortie
RUN gradle api:buildFatJar --no-daemon


FROM nginx:trixie AS runtime
RUN apt-get update && apt-get install -y \
    openjdk-21-jre-headless \
    ca-certificates \
    tini \
    bash \
    && rm -rf /var/lib/apt/lists/*
ARG API_DIR
ARG FRONTEND_DIR
ARG APP=app
RUN mkdir /${APP}
COPY --from=frontend-build /dashboard/dist /${APP}/dashboard/
COPY --from=api-build /shortie/${API_DIR}/build/libs/shortie.jar /${APP}/shortie.jar
# remove default nginx config
RUN rm -f /etc/nginx/conf.d/default.conf
COPY nginx/production.conf /etc/nginx/nginx.conf
COPY docker/entrypoint.sh /${APP}/
RUN chmod +x /${APP}/entrypoint.sh
WORKDIR /${APP}
ENTRYPOINT ["tini", "--", "./entrypoint.sh"]
