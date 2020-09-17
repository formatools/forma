import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import com.stepango.forma.AndroidTestUtil
import com.stepango.forma.TestUtil
import com.stepango.forma.Validator
import com.stepango.forma.validateName
import org.gradle.api.Project

data class AndroidFeatureConfig(
    val testInstrumentationRunnerClass: String = androidJunitRunner,
    val consumerMinificationFiles: Set<String> = emptySet(),
    val manifestPlaceholders: Map<String, Any> = emptyMap(),
    val buildConfiguration: BuildConfiguration = BuildConfiguration()
)

fun androidFeatureDefinition(
    androidFeatureConfig: AndroidFeatureConfig
) = FeatureDefinition(
    pluginName = "com.android.library",
    pluginExtension = BaseAppModuleExtension::class,
    featureConfiguration = androidFeatureConfig
) { extension, featureConfig, project, config ->
    with(extension) {
        compileSdkVersion(config.compileSdk)

        defaultConfig.applyFrom(
            config,
            featureConfig.testInstrumentationRunnerClass,
            featureConfig.consumerMinificationFiles,
            featureConfig.manifestPlaceholders
        )

        buildTypes.applyFrom(featureConfig.buildConfiguration)
        compileOptions.applyFrom(config)
    }
}

fun Project.android_test_util(
    dependencies: FormaDependency = emptyDependency()
) {
    // TODO refactor to single method call
    AndroidTestUtilValidator.validate(this)
    applyFeatures(setOf(
        testUtilFeatureDefinition
    ))

    applyDependencies(
        dependencies = dependencies
    )

    dependencies.forEach(
        projectAction = { validateName(it.project.name, AndroidTestUtil, TestUtil) }
    )
}

object AndroidTestUtilValidator : Validator {
    override fun validate(project: Project) {
        validateName(project.name, AndroidTestUtil)
    }
}