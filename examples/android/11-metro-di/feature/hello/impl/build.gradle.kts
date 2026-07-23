// Path B call site — attributes only. Metro is owned by metroImpl type.
metroImpl(
    packageName = "tools.forma.examples.android.metro.feature.hello.impl",
    dependencies = deps(
        target(":feature:hello:api"),
    )
)
