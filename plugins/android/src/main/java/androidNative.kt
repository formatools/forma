import org.gradle.api.Project
import tools.forma.android.config.NdkAbi
import tools.forma.android.config.NdkBuildSystem
import tools.forma.android.feature.AndroidNativeConfiguration
import tools.forma.android.feature.androidNativeDefinition
import tools.forma.android.feature.applyFeatures
import tools.forma.android.target.AndroidTargetRegistry
import tools.forma.android.target.AndroidTargetTypes
import tools.forma.android.validation.disallowResources
import tools.forma.android.visibility.Public
import tools.forma.android.visibility.Visibility
import tools.forma.owners.NoOwner
import tools.forma.owners.Owner
import tools.forma.validation.asValidator
import tools.forma.validation.validate

fun Project.androidNative(
    packageName: String,
    buildSystem: NdkBuildSystem,
    abi: Set<NdkAbi> = emptySet(),
    owner: Owner = NoOwner,
    visibility: Visibility = Public
) {
    disallowResources()
    AndroidTargetRegistry.selfValidator(AndroidTargetTypes.native).asValidator().validate(target)

    val configuration = AndroidNativeConfiguration(
        packageName = packageName,
        buildSystem = buildSystem,
        abi = abi
    )

    applyFeatures(
        androidNativeDefinition(configuration)
    )
}
