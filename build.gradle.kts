plugins {
    kotlin("jvm") version "1.5.10"
    groovy
    java
    `java-gradle-plugin`
    `maven-publish`
    alias(libs.plugins.licenser)
}

group = "io.github.ladysnake"
version = "0.1-SNAPSHOT"

repositories {
    mavenCentral()
    gradlePluginPortal()
    maven {
        name = "Fabric"
        setUrl("https://maven.fabricmc.net")
    }
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(gradleApi())
    implementation(libs.cursegradle)
    implementation(libs.licenser)
    implementation(libs.loom)
    implementation(libs.jgit)
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

java {
    withSourcesJar()
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

gradlePlugin {
    plugins {
        create("chenille") {
            id = "io.github.ladysnake.chenille"
            implementationClass = "io.github.ladysnake.chenille.ChenilleGradlePlugin"
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("plugin") {
            groupId = project.group.toString()
            artifactId = project.name
            version = project.version.toString()

            from(components["java"])
        }
    }
}
