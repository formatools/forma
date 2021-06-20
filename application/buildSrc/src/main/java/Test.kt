object test {
    private val junit = deps(
        "junit:junit:${versions.test.junit}".dep,
        "org.hamcrest:hamcrest-core:${versions.test.hamcrest}".dep
    )

    private val mockk = deps(
        transitiveDeps("io.mockk:mockk:${versions.test.mockk}")
    )

    private val junit_ext = deps(
        "androidx.test.ext:junit:${versions.test.junit_ext}".dep
    )

    private val espresso = deps(
        "androidx.test.espresso:espresso-core:${versions.test.espresso}".dep
    )

    val unit = deps(
        junit,
        mockk
        // Place here other Unit-test deps
    )

    val ui = deps(
        junit,
        junit_ext,
        espresso,
        "androidx.test:monitor:1.3.0".dep
        // Place here other UI-test deps
    )
}