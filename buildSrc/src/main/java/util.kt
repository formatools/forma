import com.stepango.forma.feature.FeatureDefinition
import com.stepango.forma.feature.applyFeatures
import com.stepango.forma.module.UtilModule
import com.stepango.forma.utils.applyDependencies
import com.stepango.forma.validation.validator
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
