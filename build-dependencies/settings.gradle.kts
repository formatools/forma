rootProject.name = "build-dependencies"

pluginManagement {
    includeBuild("../build-settings")
}

plugins {
    id("convention-dependencies")
}

includeBuild("../plugins")

include("dependencies")
