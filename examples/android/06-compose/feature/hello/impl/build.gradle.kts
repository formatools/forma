impl(
    packageName = "tools.forma.examples.android.compose.feature.hello.impl",
    compose = true,
    dependencies = transitiveDeps(
        "androidx.compose.runtime:runtime:1.11.4",
        "androidx.compose.ui:ui:1.11.4",
        "androidx.compose.foundation:foundation:1.11.4",
        "androidx.compose.material:material:1.11.4",
        "androidx.compose.ui:ui-tooling-preview:1.11.4",
    ) + deps(
        target(":feature:hello:api"),
        target(":common:greeting:compose-widget")
    )
)
