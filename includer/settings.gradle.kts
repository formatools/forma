rootProject.name = "includer"

pluginManagement {
    includeBuild("../build-settings")
}

plugins {
    id("convention-dependencies")
}

include("plugin")
