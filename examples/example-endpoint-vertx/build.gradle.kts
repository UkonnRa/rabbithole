plugins {
    id("example-configuration")
  application
    id("com.github.johnrengelman.shadow") version "7.1.0"
}

private object Versions {
  const val VERTX = "4.2.0.Beta1"
}

dependencies {
  implementation(project(":example-core"))

  implementation(platform("io.vertx:vertx-stack-depchain:${Versions.VERTX}"))

  implementation("io.vertx:vertx-config")
  implementation("io.vertx:vertx-config-hocon")

  testImplementation("io.vertx:vertx-junit5")
}

val mainVerticleName = "com.ukonnra.wonderland.rabbithole.example.endpoint.vertx.MainVerticle"
val watchForChange = "src/**/*.java"
val doOnChange = "$projectDir/gradlew classes"

application {
  mainClass.set("io.vertx.core.Launcher")
}

tasks {
  getByName<JavaExec>("run") {
    args = listOf(
      "run",
      mainVerticleName,
      "--redeploy=$watchForChange",
      "--launcher-class=${application.mainClass.get()}",
      "--on-redeploy=$doOnChange"
    )
  }

  shadowJar {
    archiveClassifier.set("fat")
    manifest {
      attributes["Main-Verticle"] = mainVerticleName
    }
    mergeServiceFiles()
  }
}
