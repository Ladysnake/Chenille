import dev.yumi.gradle.licenser.api.rule.HeaderRule
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.*

plugins {
    groovy
    `java-library`
    `kotlin-dsl` // kotlin version is whatever Gradle bundles right now
    `maven-publish`
    `java-gradle-plugin`
    alias(libs.plugins.gradle.pluginPublish)
    alias(libs.plugins.licenser)
}

group = "io.github.ladysnake"
version = project.property("version")!!

val functionalTest = sourceSets.register("functionalTest")

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
    implementation(gradleApi())
    compileOnly(gradleKotlinDsl())
    implementation(libs.curseforgegradle)
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
    testRuntimeOnly(libs.junit.launcher)
    "functionalTestImplementation"(platform("org.spockframework:spock-bom:2.4-groovy-4.0"))
    "functionalTestImplementation"("org.spockframework:spock-core")
    "functionalTestImplementation"(libs.junit.launcher)
}

license {
    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    val firstYear = 2022
    val year = if (currentYear == firstYear) "$currentYear" else "$firstYear-$currentYear"
    val lgplLines = file("src/main/resources/license_headers/LGPL.txt").readText()
        .replace("\\$\\{(.*?)}".toRegex()) {
            when (val g = it.groups[1]!!.value) {
                "year" -> year
                "projectDisplayName" -> project.property("display_name").toString()
                "projectOwners" -> project.property("owners").toString()
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
    compilerOptions.freeCompilerArgs.add("-jvm-default=enable")
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
    maxHeapSize = "5G"
}

val functionalTestTask = tasks.register<Test>("functionalTest") {
    description = "Runs the functional tests."
    group = "verification"
    testClassesDirs = functionalTest.get().output.classesDirs
    classpath = functionalTest.get().runtimeClasspath
    mustRunAfter(tasks.test)
}

tasks.check {
    dependsOn(functionalTestTask)
}

gradlePlugin {
    website.set("https://ladysnake.org/wiki/chenille")
    vcsUrl.set("https://github.com/ladysnake/chenille")
    testSourceSets(functionalTest.get())
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

val javaVersion = 25

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(javaVersion))
    }
}

kotlin {
    jvmToolchain(javaVersion)
}

publishing {
    publications {
        create<MavenPublication>("plugin") {
            from(components["java"])
        }
    }
}
