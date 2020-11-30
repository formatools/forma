include(":plugins")
project(":plugins").projectDir = file("../../plugins")

fun pluginTarget(name: String) {
    include(":plugins:$name")
    project(":plugins:$name").projectDir = file("../../plugins/$name")
}

pluginTarget("android")
pluginTarget("deps")
pluginTarget("deps-core")
pluginTarget("validation")
pluginTarget("target")
