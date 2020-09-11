impl(
    packageName = "com.stepango.blockme.welcome.impl",
    projectDependencies = deps(
        project(":welcome-api")
    ),
    testDependencies = deps(
        test.junit
    ),
    androidTestDependencies = deps(
        test.junit_ext,
        test.espresso
    )
)
