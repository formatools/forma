androidBinary(
    packageName = "tools.forma.sample.app",
    owner = Teams.core,
    versionCode = 1,
    versionName = "0.0.1",
    // Compose enabled so binary can host Compose content from compose-widget deps.
    compose = true,
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
        target(":common:greeting:compose-widget"),
        target(":core:mvvm:library"),
        target(":core:di:library")
    )
)
// TODO: enable when create crashlytics project
//    .withPlugins(Plugins.googleServices, Plugins.crashlytics())
