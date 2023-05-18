androidBinary(
    packageName = "com.stepango.blockme.app",
    owner = Teams.core,
    versionCode = 1,
    versionName = "0.0.1",
    dependencies = deps(
        target(":root-app"),

        target(":feature:home:api"),
        target(":feature:home:impl"),
        target(":feature:characters:core:api"),
        target(":feature:characters:core:impl"),
        target(":feature:characters:list:api"),
        target(":feature:characters:list:impl"),
        target(":feature:characters:detail:api"),
        target(":feature:characters:detail:impl"),
        target(":feature:characters:favorite:api"),
        target(":feature:characters:favorite:impl"),

        target(":common:extensions:android-util"),
//        target(":common:util-native"),
        target(":core:mvvm:library"),
        target(":core:di:library")
    )
)
// TODO: enable when create crashlytics project
//    .withPlugins(Plugins.googleServices, Plugins.crashlytics())
