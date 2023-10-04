widget(
    packageName = "tools.forma.sample.widget.toggle",
    dependencies = deps(
        androidx.appcompat
    ) + deps(
        target(":feature:home:res")
    )
)
