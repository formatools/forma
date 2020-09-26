import com.stepango.forma.UtilModule
import com.stepango.forma.validator
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

val utilFeatureDefinition = FeatureDefinition(
    pluginName = "kotlin",
    pluginExtension = KotlinJvmProjectExtension::class,
    featureConfiguration = { },
    defaultDependencies = kotlin.stdlib_jdk8
) { _, _, _, _ -> }

fun Project.util(
    dependencies: FormaDependency = emptyDependency(),
    testDependencies: FormaDependency = emptyDependency()
) {
    val nameValidator = validator(UtilModule)
    // TODO refactor to single method call
    nameValidator.validate(this)
    applyFeatures(
        utilFeatureDefinition
    )

    applyDependencies(
        validator = nameValidator,
        dependencies = dependencies,
        testDependencies = testDependencies
    )
}
