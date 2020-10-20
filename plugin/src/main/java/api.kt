import com.stepango.forma.feature.applyFeatures
import com.stepango.forma.feature.kotlinFeatureDefinition
import com.stepango.forma.feature.kotlinKaptFeatureDefinition
import com.stepango.forma.module.ApiModule
import com.stepango.forma.module.LibraryModule
import com.stepango.forma.utils.applyDependencies
import com.stepango.forma.validation.validate
import com.stepango.forma.validation.validator
import org.gradle.api.Project

fun Project.api(
    dependencies: FormaDependency = emptyDependency()
) {
    validate(ApiModule)
    applyFeatures(
        kotlinFeatureDefinition()
    )

    if (dependencies.hasConfigType(Kapt)) {
        applyFeatures(
            kotlinKaptFeatureDefinition()
        )
    }

    applyDependencies(
        validator = validator(ApiModule, LibraryModule),
        dependencies = dependencies
    )
}

