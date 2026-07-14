package tools.forma.jvm.feature

import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.kotlin.dsl.apply
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

/**
 * Minimal Kotlin JVM feature applicator for the pure JVM plugin (F-030).
 *
 * Applies `org.jetbrains.kotlin.jvm` and configures source/target compatibility + jvmTarget.
 * Does not depend on AndroidProjectSettings / Forma.settings or any Android bits.
 *
 * Default JVM target: 11 (modern default for pure JVM targets; override in future
 * jvmProjectConfiguration if added).
 */
object JvmDefaults {
    val javaVersionCompatibility: JavaVersion = JavaVersion.VERSION_11
    const val jvmTarget: String = "11"
}

fun Project.applyKotlinJvm() {
    apply(plugin = "org.jetbrains.kotlin.jvm")

    val jvmTarget = JvmDefaults.jvmTarget
    val javaVersion = JvmDefaults.javaVersionCompatibility.toString()

    tasks.withType(JavaCompile::class.java).configureEach {
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
    }

    tasks.withType(KotlinCompile::class.java).configureEach {
        kotlinOptions.jvmTarget = jvmTarget
    }
}
