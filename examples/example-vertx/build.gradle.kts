import com.google.protobuf.gradle.generateProtoTasks
import com.google.protobuf.gradle.id
import com.google.protobuf.gradle.plugins
import com.google.protobuf.gradle.protoc

plugins {
  id("example-configuration")

  application
  id("com.google.protobuf") version "0.8.17"
  id("com.github.johnrengelman.shadow") version "7.0.0"
}

private object Versions {
  const val VERTX = "4.2.0.Beta1"
  const val COROUTINE = "1.5.1"
  const val PROTOC = "4.0.0-rc-2"
  const val JAVAX_ANNOTATION = "1.3.2"
  const val GRPC = "1.40.1"
}

dependencies {
  implementation(project(":rabbithole-core"))

  implementation(project(":rabbithole-jsonapi"))
  annotationProcessor(project(":rabbithole-jsonapi"))

  implementation(project(":rabbithole-grpc"))
  annotationProcessor(project(":rabbithole-grpc"))

  implementation(platform("io.vertx:vertx-stack-depchain:${Versions.VERTX}"))

  implementation("io.vertx:vertx-config")
  implementation("io.vertx:vertx-config-hocon")

  implementation("javax.annotation:javax.annotation-api:${Versions.JAVAX_ANNOTATION}")
  implementation("com.google.protobuf:protobuf-java-util:${Versions.PROTOC}")
  implementation("io.grpc:grpc-netty-shaded:${Versions.GRPC}")
  implementation("io.grpc:grpc-protobuf:${Versions.GRPC}")
  implementation("io.grpc:grpc-stub:${Versions.GRPC}")

  testImplementation("io.vertx:vertx-junit5")
}

protobuf {
  protobuf.protoc {
    artifact = "com.google.protobuf:protoc:${Versions.PROTOC}"
  }

  protobuf.plugins {
    id("grpc") {
      artifact = "io.grpc:protoc-gen-grpc-java:${Versions.GRPC}"
    }
  }

  protobuf.generateProtoTasks {
    all().forEach {
      it.plugins {
        id("grpc")
      }
    }
  }
}

val mainVerticleName = "com.ukonnra.wonderland.rabbithole.examples.vertx.MainVerticle"
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

tasks.create("preBuild", JavaExec::class) {
  classpath = sourceSets["main"].runtimeClasspath
  mainClass.set("com.ukonnra.wonderland.rabbithole.examples.vertx.PreBuild")
}

tasks.build {
  dependsOn("preBuild")
}
