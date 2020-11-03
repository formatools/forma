import com.stepango.forma.feature.applyFeatures
import com.stepango.forma.feature.kotlinFeatureDefinition
import com.stepango.forma.feature.kotlinKaptFeatureDefinition
import com.stepango.forma.module.LibraryModule
import com.stepango.forma.module.TestUtilModule
import com.stepango.forma.module.UtilModule
import com.stepango.forma.owner.NoOwner
import com.stepango.forma.owner.Owner
import com.stepango.forma.dependencies.applyDependencies
import com.stepango.forma.validation.validate
import com.stepango.forma.validation.validator
import com.stepango.forma.visibility.Public
import com.stepango.forma.visibility.Visibility
import org.gradle.api.Project

/**
 * Can't depend on api\impl
 */
fun Project.library(
    dependencies: FormaDependency = emptyDependency(),
    owner: Owner = NoOwner,
    visibility: Visibility = Public,
    testDependencies: NamedDependency = emptyDependency()
) {
    validate(LibraryModule)

    applyFeatures(
        kotlinFeatureDefinition()
    )

    if (dependencies.hasConfigType(Kapt)) {
        applyFeatures(
            kotlinKaptFeatureDefinition()
        )
    }

    applyDependencies(
        validator = validator(UtilModule, TestUtilModule),
        dependencies = dependencies,
        testDependencies = testDependencies
    )
}