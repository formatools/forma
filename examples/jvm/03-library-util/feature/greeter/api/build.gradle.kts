import tools.forma.jvm.api

api(
    packageName = "tools.forma.examples.jvm.libutil.feature.greeter.api",
    dependencies = deps(
        target(":common:library")
    )
)
