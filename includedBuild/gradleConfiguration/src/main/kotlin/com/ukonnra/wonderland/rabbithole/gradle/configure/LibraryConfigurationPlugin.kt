package com.ukonnra.wonderland.rabbithole.gradle.configure

import org.gradle.api.Project
import org.gradle.api.plugins.JavaLibraryPlugin
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.register

open class LibraryConfigurationPlugin : ServiceConfigurationPluginBase() {
  override fun doApply(target: Project) {
    target.apply<MavenPublishPlugin>()
    target.apply<JavaLibraryPlugin>()

    target.extensions.configure<JavaPluginExtension> {
      withJavadocJar()
      withSourcesJar()
    }

    target.extensions.configure<PublishingExtension> {
      publications.register("release", MavenPublication::class) {
        from(target.components["java"])
        pom {
          licenses {
            license {
              name.set("The Apache License, Version 2.0")
              url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
            }
          }
        }
      }
    }

    target.tasks.named<Jar>(JavaPlugin.JAR_TASK_NAME) {
      enabled = true
    }
  }
}
