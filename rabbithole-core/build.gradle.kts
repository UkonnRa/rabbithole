plugins {
  id("library-configuration")
}

object Versions {
  const val GUAVA = "30.1.1-jre"
}

dependencies {
  api("org.jetbrains:annotations:22.0.0")
  api("com.google.guava:guava:${Versions.GUAVA}")
}
