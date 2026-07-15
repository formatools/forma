import tools.forma.jvm.binary

binary(
    packageName = "tools.forma.examples.jvm.apiimpl.binary",
    mainClass = "tools.forma.examples.jvm.apiimpl.binary.MainKt",
    dependencies = deps(
        target(":feature:greeter:api"),
        target(":feature:greeter:impl")
    )
)
