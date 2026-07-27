// Real module implementing the same api with a lightweight stand-in.
// Target type is impl — Gradle/Forma name must end with -impl (hence stub-impl/).
// packageName uses …stub so sources stay distinct from production impl.
// Not wired by default — see root deps comments. F-104 will swap impl→stub via flag.
impl(
    packageName = "tools.forma.examples.android.hybrid.feature.hello.stub",
    dependencies = deps(
        target(":feature:hello:api")
    )
)
