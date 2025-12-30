FROM node:22-alpine AS build
RUN mkdir /dashboard
COPY website/package.json website/.yarnrc.yml website/yarn.lock /dashboard/

WORKDIR /dashboard
RUN corepack enable
RUN yarn install --immutable

# Copy the rest of the website source
COPY website .
RUN rm -f .env # remove .env to not override envs

# Build the dashboard
RUN yarn build:dashboard
# todo: think of dynamic injection of api_url (in .env)


FROM alpine:latest AS dashboard

# install envsubst, required to env subsitute
RUN set -x && \
    apk add --update "libintl" && \
    apk add --virtual build_deps "gettext" &&  \
    cp /usr/bin/envsubst /usr/local/bin/envsubst && \
    apk del build_deps

COPY --from=build /dashboard/dist /dist
COPY website/env.template.js /env.template.js
COPY website/docker-entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh
ENTRYPOINT ["/entrypoint.sh"]
