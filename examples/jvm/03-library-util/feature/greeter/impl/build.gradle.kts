import tools.forma.jvm.impl

impl(
    packageName = "tools.forma.examples.jvm.libutil.feature.greeter.impl",
    dependencies = deps(
        target(":feature:greeter:api"),
        target(":common:util"),
        target(":common:library")
    )
)
