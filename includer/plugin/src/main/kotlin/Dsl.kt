import org.gradle.api.initialization.Settings
import tools.forma.includer.IncluderExtension

fun Settings.includer(action: IncluderExtension.() -> Unit) =
    extensions.getByType(IncluderExtension::class.java).action()
