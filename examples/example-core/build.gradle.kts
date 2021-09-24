plugins {
  id("example-configuration")
  `java-library`
}

dependencies {
  api(project(":rabbithole-core"))
  annotationProcessor(project(":rabbithole-core"))
  api(project(":rabbithole-jsonapi"))
}
