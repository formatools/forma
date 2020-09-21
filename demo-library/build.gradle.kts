androidLibrary(
    packageName = "com.stepango.mylibrary",
    dependencies = transitiveDeps(
        "androidx.core:core-ktx:1.3.1",
        "androidx.appcompat:appcompat:1.1.0",
        "com.google.android.material:material:1.1.0"
    ),
    testDependencies = deps("junit:junit:4.12")
)
