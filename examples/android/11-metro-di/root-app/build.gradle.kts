// Path B composition root — Metro graph lives here; plugin from metroApp type.
metroApp(
    packageName = "tools.forma.examples.android.metro.root",
    dependencies = deps(
        target(":root-res"),
        target(":feature:hello:api"),
        target(":feature:hello:impl"),
    )
)
