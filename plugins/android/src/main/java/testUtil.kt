import tools.forma.android.feature.applyFeatures
import tools.forma.android.feature.kotlinFeatureDefinition
import tools.forma.android.target.TestUtilTarget
import tools.forma.android.target.UtilTarget
import tools.forma.android.owner.NoOwner
import tools.forma.android.owner.Owner
import tools.forma.android.validation.disallowResources
import tools.forma.validation.validate
import tools.forma.validation.validator
import tools.forma.android.visibility.Public
import tools.forma.android.visibility.Visibility
import org.gradle.api.Project
import tools.forma.deps.applyDependencies
import tools.forma.deps.FormaDependency
import tools.forma.android.config.FormaConfigurationKey
import tools.forma.android.config.DefaultConfigurationKey

fun Project.testUtil(
    configurationKey: FormaConfigurationKey = DefaultConfigurationKey,
    packageName: String,
    owner: Owner = NoOwner,
    visibility: Visibility = Public,
    dependencies: FormaDependency = emptyDependency()
) {

    disallowResources()

    validate(TestUtilTarget)

    applyFeatures(
        kotlinFeatureDefinition(configurationKey)
    )

    applyDependencies(
        validator = validator(TestUtilTarget, UtilTarget),
        dependencies = dependencies,
        repositoriesConfiguration = Forma[configurationKey].repositories
    )
}
