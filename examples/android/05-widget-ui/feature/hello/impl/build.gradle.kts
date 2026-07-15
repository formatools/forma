impl(
    packageName = "tools.forma.examples.android.widgetui.feature.hello.impl",
    dependencies = deps(
        target(":feature:hello:api"),
        target(":common:banner:widget"),
        target(":common:ui:ui-library")
    )
)
