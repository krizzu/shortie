plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
}

group = "com.kborowy"

version = "0.0.1"

application { mainClass = "io.ktor.server.netty.EngineMain" }

kotlin { compilerOptions { optIn.add("kotlin.time.ExperimentalTime") } }

dependencies {
    implementation(libs.logback.classic)
    implementation(project.dependencies.platform(libs.exposed.bom))
    implementation(libs.bundles.ktor.app)
    implementation(libs.bundles.exposed)
    implementation(libs.bundles.koin.ktor)
    implementation(libs.kotlin.datetime)
    implementation(libs.kotlin.coroutines.core)
    implementation(libs.utils.hashing.argon2)
    implementation(libs.utils.validation.urlValidator)
    implementation(libs.utils.idGenerator.sqids)

    testImplementation(libs.bundles.tests)
}

tasks.register<JavaExec>("generateMigrations") {
    group = "shortie"
    description = "Generate migration scripts"
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass = "com.kborowy.shortie.migrations.Generate_migrationsKt"

    doFirst {
        environment["MIGRATION_NAME"] =
            project.properties["migrationName"]
                ?: error("missing migration name. Pass via -PmigrationName=vX__your_name")
    }
}
