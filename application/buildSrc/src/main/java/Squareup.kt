object squareup {
    private val logging_interceptor = deps(
        "com.squareup.okhttp3:logging-interceptor:${versions.squareup.okhttp}".dep
    )

    private val retrofit_converter = deps(
        "com.squareup.retrofit2:converter-gson:${versions.squareup.retrofit}".dep
    )

    private val okio = "com.squareup.okio:okio:2.9.0".dep

    private val okhttp = deps(
        "com.squareup.okhttp3:okhttp:${versions.squareup.okhttp}".dep,
        logging_interceptor,
        okio
    )

    val retrofit = deps(
        "com.squareup.retrofit2:retrofit:${versions.squareup.retrofit}".dep,
        retrofit_converter,
        okhttp,
        okio
    )
}