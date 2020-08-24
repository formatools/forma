// Top-level build file where you can add configuration options common to all sub-projects/modules.
val kotlin_version = "1.4.0"
buildscript {
    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:4.1.0-rc01")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.0")

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle.kts.kts files
    }
}

allprojects {
    repositories {
        google()
        jcenter()
    }
}

    Grip.configure(
    Configuration(
        minSdk = 21,
        targetSdk = 29,
        compileSdk = 29,
        kotlinVersion = kotlin_version,
        agpVersion = "4.1.0-rc01",
        versionCode = 1,
        versionName = "1.0"
    )
)

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}