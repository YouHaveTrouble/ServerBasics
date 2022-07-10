plugins {
    `java-library`
    id("io.papermc.paperweight.userdev") version "1.3.7"
    id("org.jetbrains.kotlin.plugin.lombok") version "1.6.10"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "me.youhavetrouble.serverbasics"
version = "1.3.0"
description = "Modern non-bloated essentials alternative"

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

repositories {
    mavenCentral()
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://jitpack.io")
}

dependencies {
    paperDevBundle("1.19-R0.1-SNAPSHOT")
    compileOnly("io.papermc.paper:paper-api:1.19-R0.1-SNAPSHOT")

    compileOnly("me.clip:placeholderapi:2.11.1")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")

    implementation("cloud.commandframework:cloud-paper:1.7.0")
    implementation("cloud.commandframework:cloud-minecraft-extras:1.7.0")
    implementation("cloud.commandframework:cloud-annotations:1.7.0")
    implementation("org.reflections:reflections:0.10.2")
    implementation("com.zaxxer:HikariCP:5.0.1")

    compileOnly("org.projectlombok:lombok:1.18.24")
    annotationProcessor("org.projectlombok:lombok:1.18.24")
}

tasks {
    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(17)
    }

    shadowJar {
        relocate("cloud.commandframework", "me.youhavetrouble.serverbasics.cloud.commandframework")
        relocate("io.leangen.geantyref", "me.youhavetrouble.serverbasics.io.leangen.geantyref")
        relocate("com.zaxxer", "me.youhavetrouble.serverbasics.com.zaxxer")
    }

    reobfJar {
        outputJar.set(project.layout.buildDirectory.file("libs/${rootProject.name}-${rootProject.version}.jar"))
    }

    processResources {
        filesMatching("plugin.yml") {
            expand(
                "name" to rootProject.name,
                "group" to project.group,
                "version" to project.version,
                "description" to project.description,
            )
        }
    }

}
