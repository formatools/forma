import com.stepango.forma.FormaConfiguration
import com.stepango.forma.Library
import com.stepango.forma.Validator
import com.stepango.forma.validateName
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies

@Suppress("UnstableApiUsage")
internal fun Project.android_library(
    dependencies: NamedDependency,
    projectDependencies: ProjectDependency,
    testDependencies: FormaDependency,
    buildConfiguration: BuildConfiguration,
    testInstrumentationRunner: String,
    consumerMinificationFiles: Set<String>,
    manifestPlaceholders: Map<String, Any>,
    androidTestDependencies: NamedDependency,
    formaConfiguration: FormaConfiguration,
    validator: Validator = LibraryValidator
) {
    apply(plugin = "com.android.library")
    applyLibraryConfiguration(formaConfiguration, buildConfiguration, testInstrumentationRunner, consumerMinificationFiles, manifestPlaceholders)
    applyDependencies(
        formaConfiguration = formaConfiguration,
        dependencies = dependencies,
        projectDependencies = projectDependencies,
        testDependencies = testDependencies,
        androidTestDependencies = androidTestDependencies
    )
    validator.validate(this)
}

fun Project.android_library(
    packageName: String, // TODO: manifest placeholder for package
    dependencies: NamedDependency = emptyDependency(),
    projectDependencies: ProjectDependency = emptyDependency(),
    testDependencies: NamedDependency = emptyDependency(),
    buildConfiguration: BuildConfiguration = BuildConfiguration(),
    testInstrumentationRunner: String = androidJunitRunner,
    consumerMinificationFiles: Set<String> = emptySet(),
    androidTestDependencies: NamedDependency = emptyDependency(),
    manifestPlaceholders: Map<String, Any> = emptyMap()
) {
    android_library(
        dependencies,
        projectDependencies,
        testDependencies,
        buildConfiguration,
        testInstrumentationRunner,
        consumerMinificationFiles,
        manifestPlaceholders,
        androidTestDependencies,
        Forma.configuration
    )
    apply(plugin = "kotlin-android")
    dependencies {
        kotlin.stdlib_jdk8.names.forEach {
            implementation(it.name)
        }
    }
}

object LibraryValidator: Validator {
    override fun validate(project: Project) {
        validateName(project.name, Library)
    }
}