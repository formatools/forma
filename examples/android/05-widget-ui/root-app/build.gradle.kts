androidApp(
    packageName = "tools.forma.examples.android.widgetui.root",
    dependencies = deps(
        target(":root-res"),
        target(":feature:hello:api"),
        target(":feature:hello:impl"),
        target(":common:banner:widget")
    )
)
