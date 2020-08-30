// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:${versions.agp}")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${versions.jetbrains.kotlin}")

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

Forma.configure(
    Configuration(
        minSdk = 21,
        targetSdk = 29,
        compileSdk = 29,
        kotlinVersion = versions.jetbrains.kotlin,
        agpVersion = versions.agp,
        versionCode = 1,
        versionName = "1.0"
    )
)

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}