object test {
    private val junit = deps(
        "junit:junit:${versions.test.junit}".dep,
        "org.hamcrest:hamcrest-core:${versions.test.hamcrest}".dep
    )
    private val junit_ext = deps(
        "androidx.test.ext:junit:${versions.test.junit_ext}".dep
    )

    private val espresso = deps(
        "androidx.test.espresso:espresso-core:${versions.test.espresso}".dep
    )

    val unit = deps(
        junit
        // Place here other Unit-test deps
    )

    val ui = deps(
        junit_ext,
        espresso
        // Place here other UI-test deps
    )
}