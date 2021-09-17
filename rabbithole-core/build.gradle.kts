plugins {
  id("library-configuration")
}

object Versions {
  const val SPOTBUGS = "4.4.1"
  const val AUTO_SERVICE = "1.0"
}

dependencies {
  api("com.github.spotbugs:spotbugs-annotations:${Versions.SPOTBUGS}")
  api("com.google.auto.service:auto-service:${Versions.AUTO_SERVICE}")
  annotationProcessor("com.google.auto.service:auto-service:${Versions.AUTO_SERVICE}")
}
