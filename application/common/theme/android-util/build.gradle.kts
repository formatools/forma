androidUtil(
    packageName = "com.stepango.blockme.common.theme.android.util",

    dependencies = deps(
        androidx.appcompat,
        google.dagger

    ) + kapt(
        google.dagger_compiler
    )
)