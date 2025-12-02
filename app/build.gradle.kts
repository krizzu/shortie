plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
}

group = "com.kborowy"

version = "0.0.1"

application { mainClass = "io.ktor.server.netty.EngineMain" }

dependencies {
    implementation(libs.logback.classic)
    implementation(project.dependencies.platform(libs.exposed.bom))
    implementation(libs.bundles.ktor.app)
    implementation(libs.bundles.exposed)
    implementation(libs.kotlin.datetime)
    implementation(libs.hash.argon2)

    testImplementation(libs.bundles.ktor.test)
}

tasks.register<JavaExec>("generateMigrations") {
    group = "shortie"
    description = "Generate migration scripts"
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass = "shortie.migrations.Generate_migrationsKt"

    doFirst {
        environment["MIGRATION_NAME"] =
            project.properties["migrationName"]
                ?: error("missing migration name. Pass via -PmigrationName=vX__your_name")
    }
}
