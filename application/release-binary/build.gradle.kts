import tools.forma.android.config.Release
import tools.forma.android.config.buildFields

androidBinary(
    packageName = "com.stepango.blockme.app.release",
    owner = Teams.core,
    dependencies = deps(
        target(":root-app")
        // TODO place other dependencies here for release type
    ),
    buildConfiguration = Release(
        proguardFiles = listOf("proguard-android-optimize.txt", "proguard-rules.pro"),
        buildFields = buildFields()
            .add("ENABLE_CRASHLYTICS", true)
    )
)