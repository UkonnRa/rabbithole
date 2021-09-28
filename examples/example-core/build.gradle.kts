plugins {
  id("example-configuration")
  `java-library`
}

object Versions {
  const val SPOTBUGS = "4.4.1"
}

dependencies {
  api(project(":rabbithole-processor"))
  annotationProcessor(project(":rabbithole-processor"))

  api("com.github.spotbugs:spotbugs-annotations:${Versions.SPOTBUGS}")
}
