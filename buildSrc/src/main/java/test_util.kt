import com.stepango.forma.Library
import com.stepango.forma.TestUtil
import com.stepango.forma.Validator
import com.stepango.forma.throwProjectValidationError
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

fun Project.test_util(
    dependencies: NamedDependency,
    testUtils: ProjectDependency
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
        dependencies = dependencies,
        projectDependencies = testUtils
    )
}

object TestUtilValidator : Validator {
    override fun validate(project: Project) {
        if (!TestUtil.validate(project)) {
            throwProjectValidationError(project, Library)
        }
    }
}