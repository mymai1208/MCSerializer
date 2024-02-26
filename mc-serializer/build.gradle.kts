plugins {
    kotlin("jvm")
}

group = "net.mymai1208"

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

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["kotlin"])

            groupId = "net.mymai1208"
            artifactId = "mc-serializer"
            version = version
        }
    }

    repositories {
        maven {
            name = "maven.mymai1208.net"
            url = uri("https://maven.mymai1208.net/")
            credentials {
                username = System.getenv("MAVEN_USERNAME")
                password = System.getenv("MAVEN_PASSWORD")
            }
        }
    }
}