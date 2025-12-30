FROM node:22-alpine AS build
RUN mkdir /dashboard
COPY website/package.json website/.yarnrc.yml website/yarn.lock /dashboard/

WORKDIR /dashboard
RUN corepack enable
RUN yarn install --immutable

# Copy the rest of the website source
COPY website .

# Build the dashboard
RUN yarn build:dashboard
# todo: think of dynamic injection of api_url (in .env)


FROM scratch AS dashboard
COPY --from=build /dashboard/dist /dist
