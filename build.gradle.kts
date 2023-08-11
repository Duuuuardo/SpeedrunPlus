import com.github.jengelman.gradle.plugins.shadow.tasks.ConfigureShadowRelocation
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml

buildscript {
  repositories { mavenCentral() }
  dependencies { classpath("org.yaml:snakeyaml:2.0") }
}

plugins {
  java
  id("com.github.johnrengelman.shadow") version "8.0.0"
  id("pl.allegro.tech.build.axion-release") version "1.14.4"
}

group = "com.duuuuardo"

version = scmVersion.version

val mcApiVersion: String by project
val repoRef: String by project

fun currentDateString() =
    OffsetDateTime.now(ZoneOffset.UTC).toLocalDate().format(DateTimeFormatter.ISO_DATE)

java { toolchain { languageVersion.set(JavaLanguageVersion.of(17)) } }

repositories {
  mavenCentral()
  maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
  maven("https://oss.sonatype.org/content/repositories/snapshots")
  maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
  compileOnly(group = "io.papermc.paper", name = "paper-api", version = "$mcApiVersion+")
}

tasks {
  wrapper {
    gradleVersion = "8.0.1"
    distributionType = Wrapper.DistributionType.ALL
  }

  processResources {
    val placeholders =
        mapOf(
            "version" to version,
            "apiVersion" to mcApiVersion,
        )

    filesMatching("plugin.yml") { expand(placeholders) }
    doLast {
      val resourcesDir = sourceSets.main.get().output.resourcesDir
      val yamlDumpOptions =
          DumperOptions().also {
            it.defaultFlowStyle = DumperOptions.FlowStyle.BLOCK
            it.isPrettyFlow = true
          }
      val yaml = Yaml(yamlDumpOptions)
      val pluginYml: Map<String, Any> = yaml.load(file("$resourcesDir/plugin.yml").inputStream())
      yaml.dump(
          pluginYml.filterKeys { it != "libraries" },
          file("$resourcesDir/offline-plugin.yml").writer()
      )
    }
  }

  jar { exclude("offline-plugin.yml") }

  shadowJar {
    minimize()
    archiveClassifier.set("offline")
    exclude("plugin.yml")
    rename("offline-plugin.yml", "plugin.yml")
  }

  val configureShadowRelocation by
      registering(ConfigureShadowRelocation::class) {
        target = shadowJar.get()
        prefix = "${project.group}.${project.name.lowercase()}.libraries"
      }

  build { dependsOn(shadowJar).dependsOn(configureShadowRelocation) }
}
