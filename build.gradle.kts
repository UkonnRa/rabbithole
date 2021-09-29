plugins {
    id("project-configuration")
}

object Versions {
  const val PROJECT = "0.0.1"
}

allprojects.forEach {
  it.version = Versions.PROJECT
}
