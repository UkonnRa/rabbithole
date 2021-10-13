plugins {
  id("example-configuration")
  `java-library`
}

dependencies {
  api(project(":rabbithole-core"))
  api(project(":rabbithole-plugin-jsonapi"))
  compileOnly(project(":rabbithole-processor"))
  annotationProcessor(project(":rabbithole-processor"))
}
