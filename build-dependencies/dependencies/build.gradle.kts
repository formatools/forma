plugins {
    `kotlin-dsl`
}

group = "tools.forma.demo"

dependencies {
    implementation("tools.forma:deps")
    implementation("tools.forma:owners")
    implementation("tools.forma:config")
    // F-072: Path B derived types (navigationRes) need android DSL helpers + TargetType on compile classpath
    implementation("tools.forma:android")
    implementation("tools.forma:core")
}
