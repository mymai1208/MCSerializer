pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net") { name = "Fabric" }

        mavenLocal()
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "MCSerializer"
include("mc-serializer")
include("mc-serializer-mod")
