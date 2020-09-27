import com.android.build.gradle.LibraryExtension
import com.stepango.forma.feature.FeatureDefinition
import com.stepango.forma.feature.applyFeatures
import com.stepango.forma.module.AndroidUtilModule
import com.stepango.forma.module.UtilModule
import com.stepango.forma.module.WidgetModule
import com.stepango.forma.utils.BuildConfiguration
import com.stepango.forma.utils.applyDependencies
import com.stepango.forma.utils.applyFrom
import com.stepango.forma.validation.validate
import com.stepango.forma.validation.validator
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

fun Project.widget(
    packageName: String,
    dependencies: FormaDependency = emptyDependency(),
    testDependencies: NamedDependency = emptyDependency(),
    androidTestDependencies: NamedDependency = emptyDependency(),
    testInstrumentationRunner: String = androidJunitRunner,
    buildConfiguration: BuildConfiguration = BuildConfiguration(),
    consumerMinificationFiles: Set<String> = emptySet(),
    manifestPlaceholders: Map<String, Any> = emptyMap()
) {
    validate(WidgetModule)

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
        validator = validator(WidgetModule, UtilModule, AndroidUtilModule),
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