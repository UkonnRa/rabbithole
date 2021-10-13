plugins {
  id("library-configuration")
}

dependencies {
  api("org.freemarker:freemarker:2.3.31")
  api("com.github.spotbugs:spotbugs-annotations:4.5.1")

  testImplementation("com.google.testing.compile:compile-testing:0.19")
}
