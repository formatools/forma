import com.stepango.forma.TestUtil
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

fun Project.test_util(
    dependencies: FormaDependency = emptyDependency()
) {
    val validator = validator(TestUtil)
    // TODO refactor to single method call
    validator.validate(this)
    applyFeatures(setOf(
        testUtilFeatureDefinition
    ))

    applyDependencies(
        dependencies = dependencies
    )

    dependencies.forEach(
        projectAction = { validator.validate(project) }
    )
}
