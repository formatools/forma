dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "plugins"

include("android")
include("deps")
include("deps-core")
include("target")
include("validation")
