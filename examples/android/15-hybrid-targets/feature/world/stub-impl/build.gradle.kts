// Sibling stub for the world feature — same matrix rules as impl (depends on api only).
// Directory name stub-impl satisfies the impl type suffix rule.
// Same FQNs as production impl so composition roots need no source swap (F-104).
impl(
    packageName = "tools.forma.examples.android.hybrid.feature.world.impl",
    dependencies = deps(
        target(":feature:world:api")
    )
)
