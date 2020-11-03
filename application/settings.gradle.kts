include(
    ":app",
    ":date:util",

    ":feature:home:api",
    ":feature:home:impl",
    ":feature:characters:core:api",
    ":feature:characters:core:impl",
    ":feature:characters:list:api",
    ":feature:characters:list:impl",

    ":core:di:library",
    ":core:network:library",
    ":core:mvvm:library",
    ":core:navigation:library",
    ":core:theme:android-util",

    ":common:extensions:android-util",
    ":common:extensions:util",
    ":common:extensions:databinding-adapter",
    ":common:progressbar:databinding",
    ":common:recyclerview:widget",
    ":common:util",

    ":root-library",
    ":string-test-util",
    ":time:util"
)
rootProject.name = "forma"
