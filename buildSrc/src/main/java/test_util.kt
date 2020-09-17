import com.stepango.forma.TestUtil
import com.stepango.forma.Validator
import com.stepango.forma.validateName
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
    // TODO refactor to single method call
    TestUtilValidator.validate(this)
    applyFeatures(setOf(
        testUtilFeatureDefinition
    ))

    applyDependencies(
        dependencies = dependencies
    )

    dependencies.forEach(
        projectAction = { validateName(it.project.name, TestUtil) }
    )
}

object TestUtilValidator : Validator {
    override fun validate(project: Project) {
        validateName(project.name, TestUtil)
    }
}