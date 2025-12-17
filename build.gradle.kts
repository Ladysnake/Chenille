import dev.yumi.gradle.licenser.api.rule.HeaderRule
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.*

plugins {
    kotlin("jvm") version "2.2.20"
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
    implementation(libs.jgit)
    implementation(libs.minotaur)
    compileOnly(libs.loom)
    runtimeOnly(libs.loom)
    testImplementation(libs.junit.api)
    testImplementation(libs.junit.params)
    testImplementation(libs.mockk)
    testRuntimeOnly(libs.junit.engine)
    "functionalTestImplementation"(platform("org.spockframework:spock-bom:2.0-groovy-3.0"))
    "functionalTestImplementation"("org.spockframework:spock-core")
}

license {
    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    val firstYear = 2022
    val year = if (currentYear == firstYear) "$currentYear" else "$firstYear-$currentYear"
    val lgplLines = file("src/main/resources/license_headers/LGPL.txt").readText()
        .replace("\\$\\{(.*?)}".toRegex()) {
            when (val g = it.groups[1]!!.value) {
                "year" -> year
                "projectDisplayName" -> project.properties["display_name"].toString()
                "projectOwners" -> project.properties["owners"].toString()
                "gplVersion" -> "3"
                else -> g
            }
        }
        .split("\r?\n".toRegex())
    val lgplHeader = HeaderRule.parse("LGPL", lgplLines)
    rule(lgplHeader)
}

java {
    withSourcesJar()
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions.freeCompilerArgs.add("-Xjvm-default=all")
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

gradlePlugin {
    website.set("https://ladysnake.org/wiki/chenille")
    vcsUrl.set("https://github.com/ladysnake/chenille")
    testSourceSets(functionalTest)
    plugins {
        create("chenille") {
            id = "io.github.ladysnake.chenille"
            displayName = "Chenille"
            description = "Helper plugin for Minecraft mods using the Fabric modloader"
            implementationClass = "io.github.ladysnake.chenille.ChenilleGradlePlugin"
            tags.set(listOf("fabricmc", "minecraft", "loom", "fabric-loom", "quilt-loom"))
        }
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
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
