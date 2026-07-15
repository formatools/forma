import tools.forma.jvm.binary

binary(
    packageName = "tools.forma.examples.jvm.libutil.binary",
    mainClass = "tools.forma.examples.jvm.libutil.binary.MainKt",
    dependencies = deps(
        target(":feature:greeter:api"),
        target(":feature:greeter:impl"),
        target(":common:util"),
        target(":common:library")
    )
)
