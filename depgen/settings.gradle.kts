rootProject.name = "depgen"

pluginManagement {
    includeBuild("../build-settings")
}

plugins {
    id("convention-dependencies")
}


include("plugin")
