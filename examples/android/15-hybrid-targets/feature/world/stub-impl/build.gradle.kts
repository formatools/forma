// Sibling stub for the world feature — same matrix rules as impl (depends on api only).
// Directory name stub-impl satisfies the impl type suffix rule.
impl(
    packageName = "tools.forma.examples.android.hybrid.feature.world.stub",
    dependencies = deps(
        target(":feature:world:api")
    )
)
