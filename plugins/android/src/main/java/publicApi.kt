import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.the

fun Project.getDefaultProguardFile(name: String) =
    the<CommonExtension>().getDefaultProguardFile(name)

val androidJunitRunner = "androidx.test.runner.AndroidJUnitRunner"
