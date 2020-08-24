// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:$agp_version")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version")

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
        agpVersion = agp_version,
        versionCode = 1,
        versionName = "1.0"
    )
)

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}