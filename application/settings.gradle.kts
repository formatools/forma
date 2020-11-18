include(":feature:characters:list:databinding")
include(":feature:characters:detail:api")
include(":feature:characters:detail:databinding")
include(":feature:home:databinding")
include(":root-res")
include(":core:theme:res")
include(":common:placeholder:res")
include(
    ":binary",
    ":date:util",

    ":feature:home:api",
    ":feature:home:impl",
    ":feature:characters:core:api",
    ":feature:characters:core:impl",
    ":feature:characters:list:api",
    ":feature:characters:list:impl",
    ":feature:characters:detail:impl",
    ":feature:characters:favorite:api",
    ":feature:characters:favorite:impl",
    ":feature:characters:favorite:databinding",

    ":core:di:library",
    ":core:network:library",
    ":core:mvvm:library",
    ":core:navigation:library",
    ":core:theme:android-util",

    ":common:extensions:android-util",
    ":common:extensions:util",
    ":common:extensions:databinding-adapter",
    ":common:progressbar:databinding",
    ":common:progressbar:res",
    ":common:recyclerview:widget",
    ":common:util",

    ":root-app",
    ":string-test-util",
    ":time:util"
)
rootProject.name = "forma"
