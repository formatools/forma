pluginManagement {
    includeBuild("build-settings")
}


plugins {
    id("convention-dependencies")
}

includeBuild("application")
includeBuild("depgen")
