androidBinary(
    packageName = "com.stepango.blockme.app",
    owner = Teams.core,
    dependencies = deps(
        project(":root-app"),

        project(":feature:home:api"),
        project(":feature:home:impl"),
        project(":feature:characters:core:api"),
        project(":feature:characters:core:impl"),
        project(":feature:characters:list:api"),
        project(":feature:characters:list:impl"),
        project(":feature:characters:detail:impl"),

        project(":common:extensions:android-util"),
        project(":core:mvvm:library"),
        project(":core:di:library")
    )
)