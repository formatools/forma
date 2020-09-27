import com.stepango.forma.feature.FeatureDefinition
import com.stepango.forma.feature.applyFeatures
import com.stepango.forma.module.UtilModule
import com.stepango.forma.utils.applyDependencies
import com.stepango.forma.validation.validate
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
    // TODO refactor to single method call
    validate(UtilModule)
    applyFeatures(
        utilFeatureDefinition
    )

    applyDependencies(
        validator = validator(UtilModule),
        dependencies = dependencies,
        testDependencies = testDependencies
    )
}
