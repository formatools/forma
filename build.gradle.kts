plugins {
    `kotlin-dsl`
}

val application = gradle.includedBuild("application")

tasks.register("buildApp") {
    dependsOn(application.task(":root-app:assemble"), application.task(":binary:assembleRelease"))
}
tasks.register("cleanApp") {
    dependsOn(application.task(":clean"))
}