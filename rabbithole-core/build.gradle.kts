plugins {
  id("library-configuration")
}

object Versions {
  const val GUAVA = "30.1.1-jre"
}

dependencies {
  implementation("com.google.guava:guava:${Versions.GUAVA}")
}
