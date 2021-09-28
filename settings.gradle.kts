rootProject.name = "rabbithole"

include(
  ":rabbithole-core",
  ":rabbithole-plugin-jsonapi",
  ":rabbithole-plugin-graphql",
  ":rabbithole-processor",
)

includeBuild("includedBuild/gradleConfiguration")

File(rootDir, "examples").listFiles()?.forEach {
  if (it.isDirectory && File(it, "build.gradle.kts").exists()) {
    include(":${it.name}")
    project(":${it.name}").projectDir = file("${rootDir}/examples/${it.name}")
  }
}
