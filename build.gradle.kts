Forma.configure(
    minSdk = 21,
    targetSdk = 29,
    compileSdk = 29,
    kotlinVersion = versions.jetbrains.kotlin,
    agpVersion = versions.agp,
    versionCode = 1,
    versionName = "1.0",
    repositories = {
        google()
        jcenter()
    },
    dataBinding = true
)

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
