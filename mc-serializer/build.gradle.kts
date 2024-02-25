plugins {
    kotlin("jvm")
}

val mod_version: String by project

group = "net.mymai1208"
version = mod_version

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))

    implementation("com.squareup:kotlinpoet:1.16.0")
    implementation("com.squareup:kotlinpoet-ksp:1.16.0")
    implementation("com.google.devtools.ksp:symbol-processing-api:1.9.22-1.0.17")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}