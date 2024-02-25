plugins {
    kotlin("jvm") version "1.9.22"
    id("fabric-loom") version "1.4-SNAPSHOT" apply false
    id("com.google.devtools.ksp") version "1.9.22-1.0.17" apply false
    `maven-publish`
}

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
    version = "0.1-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

kotlin {
    jvmToolchain(17)
}
