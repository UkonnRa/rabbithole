plugins {
  id("library-configuration")
}

object Versions {
  const val SPOTBUGS = "4.4.1"
}

dependencies {
  api("com.github.spotbugs:spotbugs-annotations:${Versions.SPOTBUGS}")
  testImplementation("com.google.testing.compile:compile-testing:0.19")
}
