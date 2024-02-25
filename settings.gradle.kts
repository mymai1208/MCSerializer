pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net") { name = "Fabric" }

        mavenLocal()
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}

rootProject.name = "MCSerializer"
include("serializer")
include("mod")