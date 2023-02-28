object io {
    val coil = transitiveDeps(
        "io.coil-kt:coil:${versions.coil}",
        "io.coil-kt:coil-base:${versions.coil}"
    )
}