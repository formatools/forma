plugins {
    `kotlin-dsl`
}

group = "tools.forma.demo"

dependencies {
    implementation("tools.forma:deps")
    implementation("tools.forma:owners")
    implementation("tools.forma:config")

    implementation("com.google.firebase:firebase-crashlytics-gradle:2.9.5")
}
