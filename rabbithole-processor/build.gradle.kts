plugins {
  id("library-configuration")
}

object Versions {
  const val AUTO_SERVICE = "1.0"
}

dependencies {
  api(project(":rabbithole-plugin-graphql"))
  api(project(":rabbithole-plugin-jsonapi"))

  implementation("com.google.auto.service:auto-service:${Versions.AUTO_SERVICE}")
  annotationProcessor("com.google.auto.service:auto-service:${Versions.AUTO_SERVICE}")
}
