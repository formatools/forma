import tools.forma.android.utils.BuildConfiguration

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
    ),

    /**
     * Version #1. Configuration build types
     *
     * Using simple types, pass arguments fo configuration
     */
    buildConfiguration = BuildConfiguration(
        BuildType(
            name = BuildTypeName(
                id = "debug",
                suffix = ".debug"
            ),
            singingConfig = SigningConfig(
                name = "Debug",
                keystore = "debug-keystore.properties"
            ),
        ),

        BuildType(
            name = BuildTypeName(
                id = "release",
                suffix = ".release"
            ),
            singingConfig = SigningConfig(
                name = "Live",
                keystore = "release-keystore.properties"
            ),
            optimizeConfig = OptimizeConfig(
                othersProguardRules = "proguard-rules.pro",
                shrinkCode = true,
                shrinkResorces = true
            )
        ),

        BuildType(
            name = BuildTypeName(
                id = "custom",
                suffix = ".custom"
            ),
            singingConfig = SigningConfig(
                name = "Custom",
                keystore = "debug-keystore.properties"
            )
        )
    ),

    /**
     * Version #2. Configuration build types
     *
     * Using aggregates types which could be encapsulate some boilerplate logic. For example, types names.
     */
    buildConfiguration = BuildConfiguration(
        DebugBuildType(
            suffix = ".debug"
            singingConfig = DebugSigningConfig(
                keystore = "debug-keystore.properties"
            ),
        ),

        ReleaseBuildType(
            suffix = ".release"
            singingConfig = ReleaseSigningConfig(
                keystore = "release-keystore.properties"
            ),
            optimizeConfig = OptimizeConfig(
                othersProguardRules = "proguard-rules.pro",
                shrinkCode = true,
                shrinkResorces = true
            )
        ),

        CustomBuildType(
            name = "custom",
            suffix = ".custom"
            signingConfig = SigninConfig(
                name = "Custom",
                keystore = "debug-keystore.properties"
            )
        )
    )
)