import org.gradle.api.JavaVersion

buildscript {
    androidProjectConfiguration(
        project = rootProject,
        minSdk = 23,
        targetSdk = 37,
        // F-087: compileSdk 37 for AndroidX core 1.19 AAR metadata (minCompileSdk=37)
        compileSdk = 37,
        agpVersion = "9.3.0",
        // Navigation 2.9 / modern AndroidX ship JVM 11 bytecode — cannot inline into 1.8
        javaVersionCompatibility = JavaVersion.VERSION_11,
        // F-090 / GH #97: optional allow-list for forked-in third-party modules that do
        // not use Forma suffixes. Exact Gradle path and/or project name. Default empty.
        // dependencyValidationExclusions = setOf(
        //     ":third-party:exoplayer:library-core",
        // ),
        // F-091 / GH #88: AGP BuildFeatures — all default off. Opt in intentionally, e.g.:
        // buildFeatures = tools.forma.config.FormaBuildFeatures(
        //     buildConfig = true,
        // ),
        // F-098 / GH #103: AGP core library desugaring (Java 8+ APIs on lower minSdk).
        // Project-global only — not a per-module call-site flag. Default off (sample minSdk 23).
        // coreLibraryDesugaring = true,
        // coreLibraryDesugaringDependency = "com.android.tools:desugar_jdk_libs:2.1.5", // optional override
        // F-099 / GH #126: project-global product feature flags (not AGP BuildFeatures).
        // Conditional deps: depsIf("daggerReflect", …) / depsUnless("daggerReflect", …).
        // See docs/TARGET-FEATURE-OPTIONS.md. Sample keeps default empty (all unknown = false).
        // featureFlags = tools.forma.config.FormaFeatureFlags(
        //     "daggerReflect" to true,
        // ),
        extraPlugins =
            listOf(
                libs.plugins.toolsFormaDemoDependencies,
                libs.plugins.devtoolsKspSymbolProcessing,
                libs.plugins.navigationSafeArgs,
                libs.plugins.firebaseCrashlytics
            )
    )
}
