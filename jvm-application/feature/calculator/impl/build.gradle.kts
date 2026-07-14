import tools.forma.jvm.impl

impl(
    packageName = "tools.forma.jvm.sample.feature.calculator.impl",
    dependencies = deps(
        target(":feature:calculator:api"),
        target(":common:library"),
        target(":common:util")
    )
)
