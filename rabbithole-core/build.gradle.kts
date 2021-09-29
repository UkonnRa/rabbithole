plugins {
  id("library-configuration")
}

object Versions {
  const val SPOTBUGS = "4.4.2"
//  const val FREEMARKER = "2.3.31"
}

dependencies {
  api("com.github.spotbugs:spotbugs-annotations:${Versions.SPOTBUGS}")
//  api("org.freemarker:freemarker:${Versions.FREEMARKER}")

  testImplementation("com.google.testing.compile:compile-testing:0.19")
}
