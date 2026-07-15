import tools.forma.jvm.impl

impl(
    packageName = "tools.forma.examples.jvm.multifeature.feature.greeter.impl",
    dependencies = deps(target(":feature:greeter:api"))
)
