import tools.forma.jvm.binary

binary(
    packageName = "tools.forma.examples.kmp.binary",
    mainClass = "tools.forma.examples.kmp.binary.MainKt",
    dependencies = deps(
        target(":shared-kmp-library"),
    ),
)
