import com.google.protobuf.gradle.generateProtoTasks
import com.google.protobuf.gradle.id
import com.google.protobuf.gradle.plugins
import com.google.protobuf.gradle.protoc

plugins {
  id("example-configuration")
  `java-library`
  id("com.google.protobuf") version "0.8.17"
}

private object Versions {
  const val PROTOC = "4.0.0-rc-2"
  const val JAVAX_ANNOTATION = "1.3.2"
  const val GRPC = "1.40.1"
}

dependencies {
  api(project(":rabbithole-core"))
  api(project(":rabbithole-jsonapi"))
  api(project(":rabbithole-grpc"))

  implementation("javax.annotation:javax.annotation-api:${Versions.JAVAX_ANNOTATION}")
  implementation("com.google.protobuf:protobuf-java-util:${Versions.PROTOC}")
  implementation("io.grpc:grpc-netty-shaded:${Versions.GRPC}")
  implementation("io.grpc:grpc-protobuf:${Versions.GRPC}")
  implementation("io.grpc:grpc-stub:${Versions.GRPC}")
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

tasks.create("preBuild", JavaExec::class) {
  classpath = sourceSets["main"].runtimeClasspath
  mainClass.set("com.ukonnra.wonderland.rabbithole.example.core.PreBuild")
}

tasks.build {
  dependsOn("preBuild")
}
