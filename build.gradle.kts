import java.util.*

plugins {
    kotlin("jvm") version "1.5.10"
    groovy
    java
    `java-gradle-plugin`
    `maven-publish`
    alias(libs.plugins.gradle.pluginPublish)
    alias(libs.plugins.licenser)
}

group = "io.github.ladysnake"
version = project.properties["version"]!!

val functionalTest: SourceSet by sourceSets.creating

repositories {
    mavenCentral()
    gradlePluginPortal()
    maven {
        name = "Fabric"
        setUrl("https://maven.fabricmc.net")
    }
    maven {
        name = "Quilt"
        setUrl("https://maven.quiltmc.org/repository/release")
    }
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(gradleApi())
    implementation(libs.artifactory)
    implementation(libs.cursegradle)
    implementation(libs.githubRelease)
    implementation(libs.licenser)
    implementation(libs.loom)
    implementation(libs.jgit)
    implementation(libs.minotaur)
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    "functionalTestImplementation"(platform("org.spockframework:spock-bom:2.0-groovy-3.0"))
    "functionalTestImplementation"("org.spockframework:spock-core")
}

license {
    setHeader(file("src/main/resources/license_headers/LGPL.txt"))
    newLine.set(false)
    properties {
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val firstYear = 2022
        val year = if (currentYear == firstYear) currentYear else "$firstYear-$currentYear"
        ext["year"] = year
        ext["projectDisplayName"] = project.properties["display_name"]
        ext["projectOwners"] = project.properties["owners"]
        ext["gplVersion"] = "3"
    }
}

java {
    withSourcesJar()
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
    maxHeapSize = "5G"
}

val functionalTestTask = tasks.register<Test>("functionalTest") {
    description = "Runs the functional tests."
    group = "verification"
    testClassesDirs = functionalTest.output.classesDirs
    classpath = functionalTest.runtimeClasspath
    mustRunAfter(tasks.test)
}

tasks.check {
    dependsOn(functionalTestTask)
}

pluginBundle {
    website = "https://ladysnake.github.io/wiki/chenille"
    vcsUrl = "https://github.com/Ladysnake/Chenille"
    tags = listOf("fabricmc", "minecraft", "loom", "fabric-loom")
}

gradlePlugin {
    testSourceSets(functionalTest)
    plugins {
        create("chenille") {
            id = "io.github.ladysnake.chenille"
            displayName = "Chenille"
            description = "Helper plugin for Minecraft mods using the Fabric modloader"
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
