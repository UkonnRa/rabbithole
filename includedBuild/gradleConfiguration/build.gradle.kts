plugins {
  `kotlin-dsl`
  `java-gradle-plugin`

  id("com.github.ben-manes.versions") version "0.39.0"
  id("org.jlleitschuh.gradle.ktlint") version "10.2.0"
  id("io.gitlab.arturbosch.detekt") version "1.18.1"
}

repositories {
  gradlePluginPortal()
}

dependencies {
  implementation("com.github.ben-manes:gradle-versions-plugin:0.39.0")
  implementation("com.diffplug.spotless:spotless-plugin-gradle:5.15.0")
  implementation("gradle.plugin.com.github.spotbugs.snom:spotbugs-gradle-plugin:4.7.5")
}

gradlePlugin {
  plugins.register("project-configuration") {
    id = "project-configuration"
    implementationClass = "com.ukonnra.wonderland.rabbithole.gradle.configure.ProjectConfigurationPlugin"
  }

  plugins.register("library-configuration") {
    id = "library-configuration"
    implementationClass = "com.ukonnra.wonderland.rabbithole.gradle.configure.LibraryConfigurationPlugin"
  }

  plugins.register("example-configuration") {
    id = "example-configuration"
    implementationClass = "com.ukonnra.wonderland.rabbithole.gradle.configure.ExampleConfigurationPlugin"
  }
}

ktlint {
  version.set("0.42.1")
}
