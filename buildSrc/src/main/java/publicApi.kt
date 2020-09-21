import com.android.build.gradle.BaseExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.the

fun Project.getDefaultProguardFile(name: String) = the<BaseExtension>().getDefaultProguardFile(name)

val androidJunitRunner = "androidx.test.runner.AndroidJUnitRunner"
