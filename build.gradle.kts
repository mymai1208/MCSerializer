plugins {
    kotlin("jvm") version "1.9.22"
    id("fabric-loom") version "1.4-SNAPSHOT" apply false
    id("com.google.devtools.ksp") version "1.9.22-1.0.17" apply false
    `maven-publish`
}

val mod_version: String by project

allprojects {
    repositories {
        mavenCentral()
    }
}

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test")
}

tasks.test {
    useJUnitPlatform()
}

subprojects {
    apply(plugin = "maven-publish")
    group = "net.mymai1208"
    version = mod_version

    repositories {
        mavenCentral()
    }
}

kotlin {
    jvmToolchain(17)
}
