plugins {
  id("library-configuration")
}

dependencies {
  api(project(":rabbithole-core"))
  implementation("io.swagger.core.v3:swagger-core:2.1.11")
}
