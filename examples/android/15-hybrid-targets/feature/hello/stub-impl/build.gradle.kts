// Real module implementing the same api with a lightweight stand-in.
// Target type is impl — Gradle/Forma name must end with -impl (hence stub-impl/).
// Same FQNs as production impl so composition roots need no source swap (F-104).
impl(
    packageName = "tools.forma.examples.android.hybrid.feature.hello.impl",
    dependencies = deps(
        target(":feature:hello:api")
    )
)
