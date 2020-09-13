import com.stepango.forma.TestUtil
import com.stepango.forma.Validator
import com.stepango.forma.validateName
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

fun Project.test_util(
    dependencies: FormaDependency = emptyDependency()
) {
    applyFeatures(setOf(
        FeatureDefinition(
            pluginName = "kotlin",
            pluginExtension = KotlinJvmProjectExtension::class,
            defaultDependencies = kotlin.stdlib_jdk8,
            validator = TestUtilValidator
        ) { _, _ -> }
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