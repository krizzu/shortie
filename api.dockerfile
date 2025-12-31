FROM gradle:9-jdk21-ubi10 AS cache
RUN mkdir -p /home/gradle/cache_home
RUN mkdir -p /shortie
ENV GRADLE_USER_HOME=/home/gradle/cache_home
COPY build.gradle.kts settings.gradle.kts /shortie/
COPY gradle /shortie/gradle
COPY app /shortie/app
WORKDIR /shortie
RUN gradle app:dependencies --no-daemon


FROM gradle:9-jdk21-ubi10 AS build
COPY --from=cache /home/gradle/cache_home /home/gradle/.gradle
COPY --chown=gradle:gradle . /shortie
WORKDIR /shortie
RUN gradle app:buildFatJar --no-daemon

# todo: build pages (./scripts/build-pages.sh)


FROM eclipse-temurin:21-jre AS runtime
EXPOSE 8080
RUN mkdir /api
COPY --from=build /shortie/app/build/libs/*.jar /api/shortie.jar
#ENTRYPOINT ["java","-jar","/app/shortie.jar"]
# todo: setup entry point for nginx, running ktor in background