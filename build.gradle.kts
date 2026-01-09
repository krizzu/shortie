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
        targetExclude(buildDirectory, licenseFile, "**/*.kts", "frontend")
        licenseHeaderFile(licenseFile)
        trimTrailingWhitespace()
        endWithNewline()
    }
}