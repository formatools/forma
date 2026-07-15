import tools.forma.jvm.binary

binary(
    packageName = "tools.forma.examples.jvm.multifeature.binary",
    mainClass = "tools.forma.examples.jvm.multifeature.binary.MainKt",
    dependencies = deps(
        target(":feature:greeter:api"),
        target(":feature:greeter:impl"),
        target(":feature:calc:api"),
        target(":feature:calc:impl")
    )
)
