import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.kotlin.dsl.dependencies
import org.gradle.api.artifacts.Dependency as GradleDependency

/**
 * Dependency wrapper
 * TODO: inline class
 * TODO: Annotation processor flag
 */
sealed class Dependency

object EmptyDependency : Dependency()

data class SingleDependency(val name: String) : Dependency()

data class GroupDependency(val names: List<String>) : Dependency()

fun Dependency.forEach(action: (String) -> Unit): Unit = when (this) {
    EmptyDependency -> Unit
    is SingleDependency -> action(name)
    is GroupDependency -> names.forEach(action)
}

fun Dependency.toList(): List<String> = when (this) {
    EmptyDependency -> emptyList()
    is SingleDependency -> listOf(name)
    is GroupDependency -> names
}

fun dependencies(vararg names: String): Dependency = when (names.size) {
    0 -> EmptyDependency
    1 -> SingleDependency(names.first())
    else -> GroupDependency(names.toList())
}

fun dependencies(vararg dependencies: Dependency): Dependency =
    GroupDependency(dependencies.flatMap { it.toList() })

val Set<String>.dependency: GroupDependency get() = this.toList().let(::GroupDependency)

fun Project.applyDependencies(
    dependencies: Dependency = EmptyDependency,
    testDependencies: Dependency = EmptyDependency,
    androidTestDependencies: Dependency = EmptyDependency
) {
    dependencies {
        dependencies.forEach { implementation(it) }
        testDependencies.forEach { testImplementation(it) }
        androidTestDependencies.forEach { androidTestImplementation(it) }
    }
}

fun DependencyHandler.implementation(dependencyNotation: Any): GradleDependency? =
    add("implementation", dependencyNotation)

fun DependencyHandler.testImplementation(dependencyNotation: Any): GradleDependency? =
    add("testImplementation", dependencyNotation)

fun DependencyHandler.androidTestImplementation(dependencyNotation: Any): GradleDependency? =
    add("androidTestImplementation", dependencyNotation)
