plugins {
    kotlin("jvm")
}

// Co-version with forma; for spike we do not publish the adapter.
group = "tools.forma.experimental"
version = "0.1.3-spike"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

kotlin {
    jvmToolchain(17)
}

repositories {
    mavenCentral()
}

dependencies {
    // ONLY production dependency allowed per F-041 / BAZEL-ADAPTER design:
    // adapter depends on core; core has ZERO Bazel / Gradle-Project / AGP knowledge.
    implementation("tools.forma:core:0.1.3")

    // Test only
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

// Convenience: allow running a small main to dump sample output if added later
tasks.register<JavaExec>("runSample") {
    group = "application"
    description = "Run a driver that prints generated BUILD for the jvm-application fixture"
    mainClass.set("tools.forma.bazel.BazelAdapterSampleKt")
    classpath = sourceSets["main"].runtimeClasspath
}
