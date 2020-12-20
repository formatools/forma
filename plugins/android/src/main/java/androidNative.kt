import org.gradle.api.Project
import tools.forma.android.config.NdkAbi
import tools.forma.android.config.NdkBuildSystem
import tools.forma.android.owner.NoOwner
import tools.forma.android.owner.Owner
import tools.forma.android.target.NativeTarget
import tools.forma.android.visibility.Public
import tools.forma.android.visibility.Visibility
import tools.forma.validation.validate
import tools.forma.android.validation.disallowResources
import tools.forma.android.feature.applyFeatures
import tools.forma.android.feature.androidNativeDefinition
import tools.forma.android.feature.AndroidNativeConfiguration

fun Project.androidNative(
    packageName: String,
    buildSystem: NdkBuildSystem,
    abi: Set<NdkAbi> = emptySet(),
    owner: Owner = NoOwner,
    visibility: Visibility = Public
) {
    disallowResources()
    target.validate(NativeTarget)

    val configuration = AndroidNativeConfiguration(
        packageName = packageName,
        buildSystem = buildSystem,
        abi = abi
    )

    applyFeatures(
        androidNativeDefinition(configuration)
    )
}