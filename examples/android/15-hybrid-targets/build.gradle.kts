buildscript {
    androidProjectConfiguration(
        project = rootProject,
        minSdk = 23,
        targetSdk = 37,
        compileSdk = 37,
        agpVersion = "9.3.0",
        // F-104: one project-global flag — IDE/local stub swap (default production impl).
        featureFlags = tools.forma.config.FormaFeatureFlags(
            tools.forma.deps.core.USE_FEATURE_STUBS_FLAG to
                providers.gradleProperty(tools.forma.deps.core.USE_FEATURE_STUBS_PROPERTY)
                    .map { it.toBoolean() }
                    .orElse(false)
                    .get(),
        ),
    )
}
