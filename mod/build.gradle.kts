plugins {
    kotlin("jvm")
    id("fabric-loom")
    id("com.google.devtools.ksp")
}

group = "net.mymai1208"

val minecraft_version: String by project
val yarn_mappings: String by project
val loader_version: String by project
val fabric_kotlin_version: String by project
val fabric_api_version: String by project

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    minecraft("com.mojang:minecraft:${minecraft_version}")
    mappings("net.fabricmc:yarn:${yarn_mappings}")

    modImplementation("net.fabricmc:fabric-loader:${loader_version}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${fabric_api_version}")
    modImplementation("net.fabricmc:fabric-language-kotlin:${fabric_kotlin_version}")

    implementation(project(":serializer"))
    ksp(project(":serializer"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}