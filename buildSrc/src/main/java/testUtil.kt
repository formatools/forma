import com.stepango.forma.feature.FeatureDefinition
import com.stepango.forma.feature.applyFeatures
import com.stepango.forma.feature.kotlinFeatureDefinition
import com.stepango.forma.module.TestUtilModule
import com.stepango.forma.module.UtilModule
import com.stepango.forma.utils.applyDependencies
import com.stepango.forma.validation.validate
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
    // TODO refactor to single method call
    validate(TestUtilModule)
    applyFeatures(
        kotlinFeatureDefinition()
    )

    applyDependencies(
        validator = validator(TestUtilModule, UtilModule),
        dependencies = dependencies
    )
}
