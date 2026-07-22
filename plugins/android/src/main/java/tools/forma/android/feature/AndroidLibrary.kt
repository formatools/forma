package tools.forma.android.feature

import androidJunitRunner
import com.android.build.api.dsl.LibraryExtension
import tools.forma.android.target.LibraryTargetTemplate
import tools.forma.android.utils.BuildConfiguration
import tools.forma.android.utils.applyFrom
import tools.forma.validation.Validator
import tools.forma.validation.validator

class AndroidLibraryFeatureConfiguration(
    val packageName: String,
    val buildConfiguration: BuildConfiguration = BuildConfiguration(),
    val testInstrumentationRunnerClass: String = androidJunitRunner,
    val consumerMinificationFiles: Set<String> = emptySet(),
    val manifestPlaceholders: Map<String, Any> = emptyMap(),
    val viewBinding: Boolean = false,
    /** Enable Jetpack Compose for this Android library target. */
    val compose: Boolean = false,
    val selfValidator: Validator = validator(LibraryTargetTemplate)
)

/**
 * AGP feature wiring for the `com.android.library` plugin.
 * Applied by role-specific targets (impl, uiLibrary, androidUtil, viewBinding, widget, ...).
 * This is **not** the removed `androidLibrary` DSL target (F-063).
 */
fun androidLibraryFeatureDefinition(
    featureConfiguration: AndroidLibraryFeatureConfiguration
) = FeatureDefinition(
    pluginName = "com.android.library",
    pluginExtension = LibraryExtension::class,
    featureConfiguration = featureConfiguration,
    configuration = { extension, feature, project, formaConfiguration ->
        with(extension) {
            namespace = feature.packageName
            compileSdk = formaConfiguration.compileSdk

            defaultConfig.applyFrom(
                formaConfiguration,
                feature.testInstrumentationRunnerClass,
                feature.consumerMinificationFiles,
                feature.manifestPlaceholders
            )

            sourceSets.getByName("main").java.srcDir("src/main/kotlin")
            sourceSets.getByName("test").java.srcDir("src/test/kotlin")
            sourceSets.getByName("androidTest").java.srcDir("src/androidTest/kotlin")

            buildTypes.applyFrom(feature.buildConfiguration)
            compileOptions.applyFrom(formaConfiguration)

            applyFormaBuildFeatures(
                project = project,
                defaults = formaConfiguration.buildFeatures,
                composeCompilerVersion = formaConfiguration.composeCompilerVersion,
                viewBinding = feature.viewBinding,
                compose = feature.compose,
            )
        }
        // AGP library `verify*Resources` is overly strict with Navigation safe-args
        // graphs in pure `androidRes` modules (debug APK still packages graphs correctly).
        project.tasks.matching { it.name.startsWith("verify") && it.name.endsWith("Resources") }
            .configureEach { enabled = false }
    }
)
