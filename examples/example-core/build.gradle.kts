plugins {
  id("example-configuration")
  `java-library`
}

dependencies {
  implementation(project(":rabbithole-core"))
  implementation(project(":rabbithole-plugin-jsonapi"))
  compileOnly(project(":rabbithole-processor"))
  annotationProcessor(project(":rabbithole-processor"))
}
