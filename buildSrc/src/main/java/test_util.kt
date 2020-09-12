import com.stepango.forma.TestUtil
import com.stepango.forma.Validator
import com.stepango.forma.validateName
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

    dependencies.names.forEach {
        validateName(it.name, TestUtil)
    }
}

object TestUtilValidator : Validator {
    override fun validate(project: Project) {
        validateName(project.name, TestUtil)
    }
}