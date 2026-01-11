@file:OptIn(OpenApiPreview::class)

import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import io.ktor.plugin.OpenApiPreview

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ktor)
}

group = "com.kborowy"

version = "0.0.1"

application { mainClass = "io.ktor.server.netty.EngineMain" }

ktor { fatJar { archiveFileName.set("shortie.jar") } }

// merge service files, so flyway can correctly find and apply migrations
tasks.withType<ShadowJar> {
    mergeServiceFiles()
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

tasks.withType<JavaExec> {
    val envFile = rootProject.file(".env")
    if (envFile.exists()) {
        envFile.readLines().forEach { line ->
            if (line.isNotBlank() && !line.startsWith("#")) {
                val parts = line.split("=", limit = 2)
                if (parts.size == 2) {
                    val key = parts[0].trim()
                    val value = parts[1].split("#", limit = 2)[0].trim()
                    environment(key, value)
                }
            }
        }
    }
}

kotlin { compilerOptions { optIn.add("kotlin.time.ExperimentalTime") } }

dependencies {
    implementation(libs.logback.classic)
    implementation(project.dependencies.platform(libs.exposed.bom))
    implementation(libs.bundles.ktor.app)
    implementation(libs.bundles.exposed)
    implementation(libs.bundles.koin.ktor)
    implementation(libs.kotlin.datetime)
    implementation(libs.kotlin.coroutines.core)
    implementation(libs.kotlin.serialization)
    implementation(libs.utils.hashing.argon2)
    implementation(libs.utils.validation.urlValidator)
    implementation(libs.utils.idGenerator.sqids)
    implementation(libs.dotenv.kotlin)

    testImplementation(libs.bundles.tests)
}

tasks.register<JavaExec>("generateMigrations") {
    group = "shortie"
    description = "Generate migration scripts"
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass = "Generate_migrationsKt"

    doFirst {
        environment["MIGRATION_NAME"] =
            project.properties["migrationName"]
                ?: error("missing migration name. Pass via -PmigrationName=vX__your_name")
    }
}
