import com.stepango.forma.*
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply

fun Project.impl(
    packageName: String, // TODO: manifest placeholder for package
    dependencies: NamedDependency = emptyDependency(),
    projectDependencies: ProjectDependency = emptyDependency(),
    testDependencies: NamedDependency = emptyDependency(),
    buildConfiguration: BuildConfiguration = BuildConfiguration(),
    testInstrumentationRunner: String = androidJunitRunner,
    consumerMinificationFiles: Set<String> = emptySet(),
    androidTestDependencies: NamedDependency = emptyDependency(),
    manifestPlaceholders: Map<String, Any> = emptyMap()
) = impl(
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

@Suppress("UnstableApiUsage")
internal fun Project.impl(
    dependencies: NamedDependency,
    projectDependencies: ProjectDependency,
    testDependencies: FormaDependency,
    buildConfiguration: BuildConfiguration,
    testInstrumentationRunner: String,
    consumerMinificationFiles: Set<String>,
    manifestPlaceholders: Map<String, Any>,
    androidTestDependencies: NamedDependency,
    formaConfiguration: FormaConfiguration,
    validator: Validator = ImplNameValidator
) {
    apply(plugin = "com.android.library")
    apply(plugin = "kotlin-android")

    applyLibraryConfiguration(
        formaConfiguration,
        buildConfiguration,
        testInstrumentationRunner,
        consumerMinificationFiles,
        manifestPlaceholders
    )

    applyDependencies(
        formaConfiguration = formaConfiguration,
        dependencies = dependencies,
        projectDependencies = projectDependencies,
        testDependencies = testDependencies,
        androidTestDependencies = androidTestDependencies,
        validator = ImplDepsValidator
    )

    validator.validate(this)
}

private object ImplNameValidator : Validator {

    override fun validate(project: Project) {
        if (Impl.validate(project).not()) {
            throwProjectValidationError(project, Impl)
        }
    }
}

private object ImplDepsValidator : Validator {

    override fun validate(project: Project) {
        if (Api.validate(project).not() &&
            Res.validate(project).not()
        ) {
            throwProjectDepsValidationError(project, Api, Res)
        }
    }
}