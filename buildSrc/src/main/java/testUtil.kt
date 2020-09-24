import com.stepango.forma.TestUtilModule
import com.stepango.forma.validator
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

object EmptyFeatureDefinition

val testUtilFeatureDefinition = FeatureDefinition(
    pluginName = "kotlin",
    pluginExtension = KotlinJvmProjectExtension::class,
    featureConfiguration = EmptyFeatureDefinition,
    defaultDependencies = kotlin.stdlib_jdk8
) { _, _, _, _ -> }

fun Project.testUtil(
    dependencies: FormaDependency = emptyDependency()
) {
    val nameValidator = validator(TestUtilModule)
    // TODO refactor to single method call
    nameValidator.validate(this)
    applyFeatures(
        testUtilFeatureDefinition
    )

    applyDependencies(
        validator = nameValidator,
        dependencies = dependencies
    )
}
