androidBinary(
    packageName = "com.stepango.blockme.app",
    owner = Teams.core,
    dependencies = deps(
        project(":root-app"),

        project(":feature:home:api"),
        project(":feature:home:impl"),
        project(":feature:home:databinding"),
        project(":feature:characters:core:api"),
        project(":feature:characters:core:impl"),
        project(":feature:characters:list:api"),
        project(":feature:characters:list:impl"),
        project(":feature:characters:list:databinding"),
        project(":feature:characters:detail:api"),
        project(":feature:characters:detail:impl"),
        project(":feature:characters:detail:databinding"),
        project(":feature:characters:favorite:api"),
        project(":feature:characters:favorite:impl"),
        project(":feature:characters:favorite:databinding"),

        project(":common:extensions:android-util"),
        project(":common:progressbar:databinding"),
        project(":core:mvvm:library"),
        project(":core:di:library")
    )
)