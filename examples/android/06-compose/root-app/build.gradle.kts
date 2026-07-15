androidApp(
    packageName = "tools.forma.examples.android.compose.root",
    compose = true,
    dependencies = transitiveDeps(
        "androidx.activity:activity-compose:1.8.0",
        "androidx.activity:activity-ktx:1.8.0",
        "androidx.activity:activity:1.8.0",
        "androidx.compose.runtime:runtime:1.5.4",
        "androidx.compose.ui:ui:1.5.4",
        "androidx.compose.foundation:foundation:1.5.4",
        "androidx.compose.material:material:1.5.4",
        "androidx.compose.ui:ui-tooling-preview:1.5.4",
    ) + deps(
        target(":root-res"),
        target(":feature:hello:api"),
        target(":feature:hello:impl"),
        target(":common:greeting:compose-widget")
    )
)
