import tools.forma.jvm.impl

impl(
    packageName = "tools.forma.jvm.sample.feature.greeter.impl",
    dependencies = deps(
        target(":feature:greeter:api"),
        target(":common:util"),
        target(":common:library")
    ),
    testDependencies = deps(
        target(":common:test-util")
    )
)
