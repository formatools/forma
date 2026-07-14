import tools.forma.jvm.binary

binary(
    packageName = "tools.forma.jvm.sample.binary",
    mainClass = "tools.forma.jvm.sample.binary.MainKt",
    dependencies = deps(
        target(":feature:greeter:api"),
        target(":feature:greeter:impl"),
        target(":feature:calculator:api"),
        target(":feature:calculator:impl"),
        target(":common:util"),
        target(":common:library")
    )
)
