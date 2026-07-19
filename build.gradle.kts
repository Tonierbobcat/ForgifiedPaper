import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import xyz.jpenilla.resourcefactory.bukkit.BukkitPluginYaml

plugins {
    `java-library`
    `maven-publish`
    id("com.vanniktech.maven.publish") version "0.33.0"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.18"
    id("xyz.jpenilla.resource-factory-bukkit-convention") version "1.3.0"
}

group = "com.loficostudios"
version = "0.1.3"
description = "forgified-paper"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

mavenPublishing {
    coordinates("com.loficostudios", "forgified-paper", "0.1.3")

    pom {
        name = "Forgified Paper"
        description = "Advanced Plugin Development."
        inceptionYear = "2025"
        url = "https://github.com/Tonierbobcat/ForgifiedPaper"
        licenses {
            license {
                name = "The Apache License, Version 2.0"
                url = "http://www.apache.org/licenses/LICENSE-2.0.txt"
                distribution = "http://www.apache.org/licenses/LICENSE-2.0.txt"
            }
        }
        developers {
            developer {
                id = "tonierbobcat"
                name = "Tonierbobcat"
                url = "https://github.com/Tonierbobcat"
            }
        }
        scm {
            url = "https://github.com/Tonierbobcat/ForgifiedPaper"
            connection = "scm:git:git://github.com/Tonierbobcat/ForgifiedPaper.git"
            developerConnection = "scm:git:ssh://git@github.com/Tonierbobcat/ForgifiedPaper.git"
        }
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