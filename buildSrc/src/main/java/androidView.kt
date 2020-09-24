import com.android.build.gradle.LibraryExtension
import com.stepango.forma.EmptyValidator
import com.stepango.forma.Validator
import com.stepango.forma.WidgetModule
import com.stepango.forma.validator
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

fun Project.view(
    packageName: String,
    dependencies: FormaDependency = emptyDependency(),
    testDependencies: NamedDependency = emptyDependency(),
    androidTestDependencies: NamedDependency = emptyDependency(),
    testInstrumentationRunner: String = androidJunitRunner,
    buildConfiguration: BuildConfiguration = BuildConfiguration(),
    consumerMinificationFiles: Set<String> = emptySet(),
    manifestPlaceholders: Map<String, Any> = emptyMap()
) {
    val nameValidator = validator(WidgetModule)
    nameValidator.validate(this)

    val viewFeatureConfiguration = ViewFeatureConfiguration(
        packageName,
        buildConfiguration,
        testInstrumentationRunner,
        consumerMinificationFiles,
        manifestPlaceholders
    )

    applyFeatures(
        applyViewFeatureDefinition(viewFeatureConfiguration)
    )

    apply(plugin = "kotlin-android")

    applyDependencies(
        validator = nameValidator,
        dependencies = dependencies,
        testDependencies = testDependencies,
        androidTestDependencies = androidTestDependencies
    )
}

class ViewFeatureConfiguration(
    val packageName: String,
    val buildConfiguration: BuildConfiguration,
    val testInstrumentationRunnerClass: String,
    val consumerMinificationFiles: Set<String>,
    val manifestPlaceholders: Map<String, Any> = emptyMap()
)

fun applyViewFeatureDefinition(
    featureConfiguration: ViewFeatureConfiguration
) = FeatureDefinition(
    pluginName = "com.android.library",
    pluginExtension = LibraryExtension::class,
    featureConfiguration = featureConfiguration,
    configuration = { extension, feature, project, formaConfiguration ->
        with(extension) {
            compileSdkVersion(formaConfiguration.compileSdk)

            defaultConfig.applyFrom(
                formaConfiguration,
                feature.testInstrumentationRunnerClass,
                feature.consumerMinificationFiles,
                feature.manifestPlaceholders
            )

            buildTypes.applyFrom(feature.buildConfiguration)
            compileOptions.applyFrom(formaConfiguration)

            project.tasks.withType(KotlinCompile::class.java).all {
                kotlinOptions.jvmTarget = formaConfiguration.javaVersionCompatibility.toString()
            }
        }
    }
)