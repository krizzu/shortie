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
    implementation(libs.bundles.ktor)
    implementation(libs.bundles.exposed)

    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.kotlin.test.junit)
}
