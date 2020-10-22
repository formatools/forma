androidLibrary(
    packageName = "com.stepango.blockme.core.di.library",
    dependencies = deps(
        google.dagger
    ) + kapt(
        google.dagger_compiler
    )
)