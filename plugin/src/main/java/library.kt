import com.stepango.forma.feature.applyFeatures
import com.stepango.forma.feature.kotlinFeatureDefinition
import com.stepango.forma.module.LibraryModule
import com.stepango.forma.module.TestUtilModule
import com.stepango.forma.module.UtilModule
import com.stepango.forma.owner.NoOwner
import com.stepango.forma.owner.Owner
import com.stepango.forma.utils.applyDependencies
import com.stepango.forma.validation.validate
import com.stepango.forma.validation.validator
import org.gradle.api.Project

fun Project.library(
    dependencies: FormaDependency = emptyDependency(),
    owner: Owner = NoOwner,
    testDependencies: NamedDependency = emptyDependency()
) {
    validate(LibraryModule)

    applyFeatures(
        kotlinFeatureDefinition()

    )

    applyDependencies(
        validator = validator(UtilModule, TestUtilModule),
        dependencies = dependencies,
        testDependencies = testDependencies
    )
}