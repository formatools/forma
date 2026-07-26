// Progressive KMP example 01: shared kmpLibrary + JVM binary consumer (pure KMP+JVM).
// Platforms configured once here; modules never list kotlin { targets }.
// kmpProjectConfiguration is default-package (parity with androidProjectConfiguration)
// so it resolves inside buildscript { } without imports.
buildscript {
    kmpProjectConfiguration(
        project = rootProject,
        platforms = tools.forma.kmp.settings.KmpPlatforms(jvm = true, android = false),
    )
}
