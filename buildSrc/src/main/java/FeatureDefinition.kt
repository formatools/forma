import com.stepango.forma.FormaConfiguration
import com.stepango.forma.Validator
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.the
import kotlin.reflect.KClass

data class FeatureDefinition<T : Any>(
    val pluginName: String,
    val pluginExtension: KClass<T>,
    val defaultDependencies: NamedDependency, // TODO do we need projects here?
    val validator: Validator,
    val configuration: (T, FormaConfiguration) -> Unit
) {
    fun applyConfiguration(project: Project, conf: FormaConfiguration) {
        configuration(project.the(pluginExtension), conf)
    }
}

fun Project.applyFeatures(
    features: Set<FeatureDefinition<*>>,
    conf: FormaConfiguration = Forma.configuration
) {
    features.forEach { definition ->
        apply(plugin = definition.pluginName)
        definition.applyConfiguration(this, conf)
        definition.defaultDependencies.names.forEach {
            dependencies.implementation(it.name)
        }
    }
}