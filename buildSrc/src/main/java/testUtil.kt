import com.stepango.forma.feature.FeatureDefinition
import com.stepango.forma.feature.applyFeatures
import com.stepango.forma.feature.kotlinFeatureDefinition
import com.stepango.forma.module.TestUtilModule
import com.stepango.forma.utils.applyDependencies
import com.stepango.forma.validation.validator
import org.gradle.api.Project

val emptyFeatureDefinition = FeatureDefinition<Any, Any>(
    pluginName = "",
    pluginExtension = Any::class,
    featureConfiguration = { },
    configuration = { _, _, _, _ -> }
)

fun Project.testUtil(
    dependencies: FormaDependency = emptyDependency()
) {
    val nameValidator = validator(TestUtilModule)
    // TODO refactor to single method call
    nameValidator.validate(this)
    applyFeatures(
        kotlinFeatureDefinition()
    )

    applyDependencies(
        validator = nameValidator,
        dependencies = dependencies
    )
}
