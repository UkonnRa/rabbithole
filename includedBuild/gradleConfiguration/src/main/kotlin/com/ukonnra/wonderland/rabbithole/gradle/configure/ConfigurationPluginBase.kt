package com.ukonnra.wonderland.rabbithole.gradle.configure

import com.diffplug.gradle.spotless.SpotlessExtension
import com.diffplug.gradle.spotless.SpotlessPlugin
import com.github.benmanes.gradle.versions.VersionsPlugin
import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import com.github.spotbugs.snom.SpotBugsPlugin
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.plugins.quality.CheckstyleExtension
import org.gradle.api.plugins.quality.CheckstylePlugin
import org.gradle.api.tasks.Delete
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.named
import org.gradle.plugins.ide.idea.IdeaPlugin
import org.gradle.testing.jacoco.plugins.JacocoPlugin
import org.gradle.testing.jacoco.plugins.JacocoPluginExtension

abstract class ConfigurationPluginBase : Plugin<Project> {
  companion object {
    internal val JAVA_VERSION = JavaVersion.VERSION_16
  }

  override fun apply(target: Project) {
    target.apply<IdeaPlugin>()
    target.apply<JavaPlugin>()
    target.apply<JacocoPlugin>()
    target.apply<VersionsPlugin>()
    target.apply<SpotlessPlugin>()
    target.apply<CheckstylePlugin>()
    target.apply<SpotBugsPlugin>()

    target.group = "com.ukonnra.wonderland"

    target.repositories.apply {
      mavenCentral()
    }

    target.extensions.configure<JavaPluginExtension> {
      sourceCompatibility = JAVA_VERSION
      targetCompatibility = JAVA_VERSION
    }

    target.extensions.configure<JacocoPluginExtension> {
      toolVersion = "0.8.7"
    }

    target.extensions.configure<SpotlessExtension> {
      java {
        importOrder()
        removeUnusedImports()
        googleJavaFormat()
      }
    }

    target.extensions.configure<CheckstyleExtension> {
      toolVersion = "9.0"
    }

    target.tasks.named<Test>(JavaPlugin.TEST_TASK_NAME) {
      finalizedBy(target.tasks.named("jacocoTestReport"))
      useJUnitPlatform()
    }

    target.tasks.named<Delete>("clean") {
      delete("out", "logs")
    }

    target.tasks.named<DependencyUpdatesTask>("dependencyUpdates") {
      checkForGradleUpdate = false
    }
  }
}