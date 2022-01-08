plugins {
  `java-library`
  id("io.papermc.paperweight.userdev") version "1.3.3"
  id("org.jetbrains.kotlin.plugin.lombok") version "1.6.10"
}

group = "eu.endermite.serverbasics"
version = "1.1.0"
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
  paperDevBundle("1.18.1-R0.1-SNAPSHOT")
  compileOnly("me.clip:placeholderapi:2.11.1")

  implementation("net.kyori:adventure-api:4.9.3")
  implementation("net.kyori:adventure-text-minimessage:4.1.0-SNAPSHOT")

  implementation("cloud.commandframework:cloud-bukkit:1.5.0")
  implementation("cloud.commandframework:cloud-paper:1.5.0")
  implementation("cloud.commandframework:cloud-minecraft-extras:1.5.0")
  implementation("cloud.commandframework:cloud-annotations:1.5.0")

  implementation("org.reflections:reflections:0.9.12")

  compileOnly("org.projectlombok:lombok:1.18.20")

  implementation("com.zaxxer:HikariCP:5.0.0")

  compileOnly("com.github.MilkBowl:VaultAPI:1.7")


}

tasks {
  assemble {
    dependsOn(reobfJar)
  }

  compileJava {
    options.encoding = Charsets.UTF_8.name()
    options.release.set(17)
  }

  processResources {
    expand(
      "name" to rootProject.name,
      "group" to project.group,
      "version" to project.version,
      "description" to project.description,
    )
  }
}
