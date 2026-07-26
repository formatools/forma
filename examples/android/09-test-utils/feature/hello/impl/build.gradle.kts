impl(
    packageName = "tools.forma.examples.android.testutils.feature.hello.impl",
    dependencies = deps(
        target(":feature:hello:api"),
        // Allowed by matrix: impl may depend on test-util (shared helpers visible to unit tests).
        target(":common:test-util"),
    ),
    // deps() is non-transitive; junit needs hamcrest → use transitiveDeps
    testDependencies = transitiveDeps(
        "junit:junit:4.13.2",
    ),
    // First-party androidTestUtil via androidTest classpath (not main matrix).
    // FormaDependency accepts project targets + named GAVs after F-113.
    androidTestDependencies = deps(
        target(":common:android-test-util"),
    ) + transitiveDeps(
        "androidx.test.ext:junit:1.2.1",
        "androidx.test:runner:1.6.2",
    ),
)
