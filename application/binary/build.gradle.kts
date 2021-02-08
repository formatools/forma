import tools.forma.android.utils.BuildConfiguration
import tools.forma.android.utils.BuildTypeConfiguration
import tools.forma.android.utils.SigningConfiguration

androidBinary(
    packageName = "com.stepango.blockme.app",
    owner = Teams.core,
    dependencies = deps(
        target(":root-app"),

        target(":feature:home:api"),
        target(":feature:home:impl"),
        target(":feature:home:databinding"),
        target(":feature:characters:core:api"),
        target(":feature:characters:core:impl"),
        target(":feature:characters:list:api"),
        target(":feature:characters:list:impl"),
        target(":feature:characters:list:databinding"),
        target(":feature:characters:detail:api"),
        target(":feature:characters:detail:impl"),
        target(":feature:characters:detail:databinding"),
        target(":feature:characters:favorite:api"),
        target(":feature:characters:favorite:impl"),
        target(":feature:characters:favorite:databinding"),

        target(":common:extensions:android-util"),
        target(":common:progressbar:databinding"),
        target(":common:util-native"),
        target(":core:mvvm:library"),
        target(":core:di:library")
    ),
    buildConfiguration = BuildConfiguration(
        type = BuildTypeConfiguration(
            name = "debug",
            testCoverageEnabled = true
        ),
        signingConfig = SigningConfiguration(
            keystore = "keystore.properties"
        ),
        fieldConfig = mutableListOf(
            "value" to true.toString()
        )
    )
)
// TODO: enable when create crashlytics project
//    .withPlugins(Plugins.googleServices, Plugins.crashlytics())