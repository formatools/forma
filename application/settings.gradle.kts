include(
    ":app",
    ":date:util",

    ":feature:home:api",
    ":feature:home:impl",
    ":feature:character:core:api",
    ":feature:character:core:impl",

    ":core:di:library",
    ":core:network:library",
    ":core:mvvm:library",
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
