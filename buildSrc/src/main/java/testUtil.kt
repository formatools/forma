import com.stepango.forma.TestUtilModule
import com.stepango.forma.feature.kotlinFeatureDefinition
import com.stepango.forma.validator
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
