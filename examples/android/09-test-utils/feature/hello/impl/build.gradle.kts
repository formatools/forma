impl(
    packageName = "tools.forma.examples.android.testutils.feature.hello.impl",
    dependencies = deps(
        target(":feature:hello:api"),
        // Allowed by matrix: impl may depend on test-util (shared helpers visible to unit tests).
        target(":common:test-util")
    ),
    // deps() is non-transitive; junit needs hamcrest → use transitiveDeps
    testDependencies = transitiveDeps(
        "junit:junit:4.13.2"
    )
)
