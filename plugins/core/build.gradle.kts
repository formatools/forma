plugins {
    kotlin("jvm")
}

// Library (not a settings/application plugin). Use same group/version as plugins for the jar.
group = "tools.forma"
version = "0.1.3"

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
