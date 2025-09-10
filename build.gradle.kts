import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import xyz.jpenilla.resourcefactory.bukkit.BukkitPluginYaml

plugins {
    `java-library`
    `maven-publish`
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.18"
    id("xyz.jpenilla.resource-factory-bukkit-convention") version "1.3.0"
}

group = "com.loficostudios"
version = "0.1.0"
description = "forgified-paper"

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            groupId = "com.loficostudios"
            artifactId = "forgified-paper"
            version = "0.1.0"
        }
    }
    repositories {
        mavenLocal()
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()
    maven {
        name = "papermc"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

dependencies {
    paperweight.paperDevBundle("1.21.8-R0.1-SNAPSHOT")
}

tasks.named<ShadowJar>("shadowJar") {
    archiveClassifier.set("")
    mergeServiceFiles()
}

tasks.build {
    dependsOn(tasks.shadowJar)
}

tasks.processResources {
    from(sourceSets.main.get().resources.srcDirs) {
    }
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

bukkitPluginYaml {
    main = "com.loficostudios.forgified.paper.ForgifiedPaper"
    load = BukkitPluginYaml.PluginLoadOrder.STARTUP
    authors.add("Tonierbobcat")
    apiVersion = "1.21.8"
}