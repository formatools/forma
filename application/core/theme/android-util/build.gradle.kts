androidUtil(
    packageName = "com.stepango.blockme.core.theme.android.util",

    dependencies = deps(
        androidx.appcompat,
        google.dagger

    ) + kapt(
        google.dagger_compiler
    )
)