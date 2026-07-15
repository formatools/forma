import tools.forma.jvm.impl

impl(
    packageName = "tools.forma.examples.jvm.testutil.feature.greeter.impl",
    dependencies = deps(
        target(":feature:greeter:api")
    ),
    testDependencies = deps(
        target(":common:test-util")
    )
)
