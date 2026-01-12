import groovy.json.JsonOutput
import groovy.json.JsonSlurper

plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.ktor) apply false
    alias(libs.plugins.spotless)
}

spotless {
    val buildDirectory = layout.buildDirectory.asFileTree
    val licenseFile = rootProject.file("spotless/license.txt")
    kotlin {
        target("api/**/*.kt", "api/**/*.kts")
        ktfmt().kotlinlangStyle()
        targetExclude(buildDirectory, licenseFile, "version.json", "**/*.kts", "frontend")
        licenseHeaderFile(licenseFile)
        trimTrailingWhitespace()
        endWithNewline()
    }
}

tasks.register<BumpVersionTask>("bumpMajor") {
    type.set("major")
    group = "shortie"
    description = "bumps major version in version.json file"
}

tasks.register<BumpVersionTask>("bumpMinor") {
    type.set("minor")
    group = "shortie"
    description = "bumps minor version in version.json file"
}

tasks.register<BumpVersionTask>("bumpPatch") {
    type.set("patch")
    group = "shortie"
    description = "bumps patch version in version.json file"
}

abstract class BumpVersionTask : DefaultTask() {

    @get:Input abstract val type: Property<String> // major, minor, patch\

    data class Version(var major: Int, var minor: Int, var patch: Int)

    @TaskAction
    fun bump() {
        val versionFile = File(project.rootDir, "version.txt")
        val version =
            (versionFile.readText().trim())
                .split('.')
                .map { it.toInt() }
                .let { Version(major = it[0], minor = it[1], patch = it[2]) }

        when (type.get().lowercase()) {
            "major" -> {
                version.major++
                version.minor = 0
                version.patch = 0
            }
            "minor" -> {
                version.minor++
                version.patch = 0
            }
            "patch" -> {
                version.patch++
            }
        }

        val newVersion = "${version.major}.${version.minor}.${version.patch}"
        versionFile.writeText(newVersion)
    }
}
