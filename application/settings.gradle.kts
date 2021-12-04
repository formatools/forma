dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositories {
        google()
        jcenter()
    }
}

rootProject.name = "forma"

apply(from = "core/modules.gradle.kts")

include(":toggle-widget")
include(":feature:home:res")
include(":feature:characters:list:databinding")
include(":feature:characters:detail:api")
include(":feature:characters:detail:res")
include(":feature:characters:detail:databinding")
include(":feature:home:databinding")
include(":root-res")
include(":common:placeholder:res")
include(":common:util-native")
include(
    ":binary",

    ":feature:home:api",
    ":feature:home:impl",
    ":feature:characters:core:api",
    ":feature:characters:core:impl",
    ":feature:characters:list:api",
    ":feature:characters:list:res",
    ":feature:characters:list:impl",
    ":feature:characters:detail:impl",
    ":feature:characters:favorite:api",
    ":feature:characters:favorite:res",
    ":feature:characters:favorite:impl",
    ":feature:characters:favorite:databinding",

    ":common:extensions:android-util",
    ":common:extensions:util",
    ":common:extensions:databinding-adapter",
    ":common:progressbar:databinding",
    ":common:progressbar:res",
    ":common:recyclerview:widget",
    ":common:util",

    ":root-app"
)
