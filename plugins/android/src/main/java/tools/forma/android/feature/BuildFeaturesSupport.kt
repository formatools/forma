package tools.forma.android.feature

import com.android.build.api.dsl.ApplicationBuildFeatures
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.LibraryBuildFeatures
import org.gradle.api.Project
import tools.forma.config.FormaBuildFeatures
import tools.forma.config.ResolvedFormaBuildFeatures
import tools.forma.config.resolveWith

/**
 * Apply Forma-resolved AGP [com.android.build.api.dsl.BuildFeatures] to a library or app extension.
 *
 * Starts from project-global [FormaBuildFeatures] defaults (all off unless configured),
 * then applies type/call-site overrides for [viewBinding] and [compose]. Every supported
 * flag is set explicitly so AGP platform defaults cannot silently enable features (GH #88).
 *
 * When [compose] is true, also runs [enableCompose] (compiler plugin + composeOptions).
 * Setting `buildFeatures.compose = true` alone is not enough on modern Kotlin/AGP.
 *
 * Public AGP DSL only — never `internal.dsl`.
 *
 * Note: `dataBinding` lives on [LibraryBuildFeatures] / [ApplicationBuildFeatures], not the
 * base [com.android.build.api.dsl.BuildFeatures] interface (AGP 9).
 */
@Suppress("UnstableApiUsage")
internal fun CommonExtension.applyFormaBuildFeatures(
    project: Project,
    defaults: FormaBuildFeatures,
    composeCompilerVersion: String,
    viewBinding: Boolean,
    compose: Boolean,
) {
    val resolved: ResolvedFormaBuildFeatures =
        defaults.resolveWith(viewBinding = viewBinding, compose = compose)
    applyResolvedBuildFeatures(project, composeCompilerVersion, resolved)
}

/**
 * Write a fully resolved feature set onto the AGP extension (testable via [resolveWith]).
 */
@Suppress("UnstableApiUsage")
internal fun CommonExtension.applyResolvedBuildFeatures(
    project: Project,
    composeCompilerVersion: String,
    resolved: ResolvedFormaBuildFeatures,
) {
    with(buildFeatures) {
        aidl = resolved.aidl
        buildConfig = resolved.buildConfig
        prefab = resolved.prefab
        resValues = resolved.resValues
        shaders = resolved.shaders
        viewBinding = resolved.viewBinding
        // dataBinding is library/app-specific in AGP 9 public API
        when (this) {
            is LibraryBuildFeatures -> dataBinding = resolved.dataBinding
            is ApplicationBuildFeatures -> dataBinding = resolved.dataBinding
        }
    }
    if (resolved.compose) {
        enableCompose(project, composeCompilerVersion)
    } else {
        buildFeatures.compose = false
    }
}
