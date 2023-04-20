buildscript {
    androidProjectConfiguration(
        project = project,
        minSdk = 21,
        targetSdk = 31,
        compileSdk = 31,
        kotlinVersion = embeddedKotlinVersion,
        agpVersion = "7.4.2"
        dataBinding = true,
        extraPlugins = listOf(
            "androidx.navigation:navigation-safe-args-gradle-plugin:2.5.3",
            "com.google.firebase:firebase-crashlytics-gradle:2.9.4",
            "tools.forma.demo:dependencies",
        )
    )
}


tasks.register("build") {
    dependsOn(subprojects.mapNotNull { project ->
        project.tasks.findByName(name)?.run {
            project.absoluteProjectPath(name)
        }
    })
}

tasks.register("check") {
    dependsOn(subprojects.mapNotNull { project ->
        project.tasks.findByName(name)?.run {
            project.absoluteProjectPath(name)
        }
    })
}

tasks.named("clean") {
    dependsOn(subprojects.mapNotNull { project ->
        project.tasks.findByName(name)?.run {
            project.absoluteProjectPath(name)
        }
    })
}
