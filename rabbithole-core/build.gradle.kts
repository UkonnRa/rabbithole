plugins {
  id("library-configuration")
}

object Versions {
  const val GUAVA = "30.1.1-jre"
  const val SPOTBUGS = "4.4.1"
}

dependencies {
  api("com.github.spotbugs:spotbugs-annotations:${Versions.SPOTBUGS}")
}
