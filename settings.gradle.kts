rootProject.name = "rabbithole"

include(
    ":rabbithole-core",
    ":rabbithole-grpc",
    ":rabbithole-jsonapi",
    ":rabbithole-graphql",
)

includeBuild("includedBuild/gradleConfiguration")

File(rootDir, "examples").listFiles()?.forEach {
  if (it.isDirectory && File(it, "build.gradle.kts").exists()) {
    include(":${it.name}")
    project(":${it.name}").projectDir = file("${rootDir}/examples/${it.name}")
  }
}
