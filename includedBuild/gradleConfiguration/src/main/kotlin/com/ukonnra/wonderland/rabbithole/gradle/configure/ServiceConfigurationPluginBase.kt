package com.ukonnra.wonderland.rabbithole.gradle.configure

import io.freefair.gradle.plugins.lombok.LombokExtension
import io.freefair.gradle.plugins.lombok.LombokPlugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

abstract class ServiceConfigurationPluginBase : ConfigurationPluginBase() {
  companion object {
    private const val JUNIT5_VERSION = "5.8.2"
    private const val LOG4J2_VERSION = "2.14.1"
    private const val JACKSON_VERSION = "2.13.0"
  }

  abstract fun doApply(target: Project)

  override fun apply(target: Project) {
    super.apply(target)

    doApply(target)

    target.apply<LombokPlugin>()

    target.extensions.configure<LombokExtension> {
      version.set("1.18.22")
    }

    target.dependencies {
      "implementation"(platform("org.junit:junit-bom:$JUNIT5_VERSION"))
      "implementation"(platform("org.apache.logging.log4j:log4j-bom:$LOG4J2_VERSION"))
      "implementation"(platform("com.fasterxml.jackson:jackson-bom:$JACKSON_VERSION"))

      "implementation"("org.apache.logging.log4j:log4j-api")
      "implementation"("org.apache.logging.log4j:log4j-core")
      "implementation"("org.apache.logging.log4j:log4j-slf4j-impl")

      "testImplementation"("org.junit.jupiter:junit-jupiter")
    }
  }
}
