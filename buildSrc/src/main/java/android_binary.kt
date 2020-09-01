import com.stepango.forma.Binary
import com.stepango.forma.FormaConfiguration
import com.stepango.forma.Validator
import com.stepango.forma.throwProjectValidationError
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply

/**
 * Application entry point. Manifest + minimal set of resources + root android project com.stepango.forma.internal.getDependency only.
 * No library dependencies, no source code.
 */
fun Project.android_binary(
    packageName: String,
    projectDependencies: ProjectDependency = emptyDependency(),
    buildConfiguration: BuildConfiguration = BuildConfiguration(),
    consumerMinificationFiles: Set<String> = emptySet(),
    manifestPlaceholders: Map<String, Any>  = emptyMap(),
    testInstrumentationRunner: String = androidJunitRunner
) = android_binary(
    packageName,
    projectDependencies,
    buildConfiguration,
    testInstrumentationRunner,
    consumerMinificationFiles,
    manifestPlaceholders,
    Forma.configuration
)

private fun Project.android_binary(
    packageName: String,
    projectDependencies: ProjectDependency,
    buildConfiguration: BuildConfiguration,
    testInstrumentationRunner: String,
    consumerMinificationFiles: Set<String>,
    manifestPlaceholders: Map<String, Any> = emptyMap(),
    formaConfiguration: FormaConfiguration = Forma.configuration,
    validator: Validator = BinaryValidator
) {
    apply(plugin = "com.android.application")
    applyAppConfiguration(
        formaConfiguration,
        packageName,
        buildConfiguration,
        testInstrumentationRunner,
        consumerMinificationFiles,
        manifestPlaceholders
    )
    applyDependencies(
        formaConfiguration = formaConfiguration,
        projectDependencies = projectDependencies
    )
    //TODO: maybe separate name validation from dependencies validation for perf reasons
    validator.validate(this)
}

object BinaryValidator: Validator {
    override fun validate(project: Project) {
        if (!Binary.validate(project)) {
            throwProjectValidationError(project, Binary)
        }
        //TODO dependencies validation
    }
}
