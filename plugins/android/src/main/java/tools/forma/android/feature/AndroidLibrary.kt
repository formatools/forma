package tools.forma.android.feature

import androidJunitRunner
import com.android.build.gradle.LibraryExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.support.listFilesOrdered
import org.xml.sax.InputSource
import tools.forma.android.target.LibraryTargetTemplate
import tools.forma.android.utils.BuildConfiguration
import tools.forma.android.utils.applyFrom
import tools.forma.validation.Validator
import tools.forma.validation.validator
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

class AndroidLibraryFeatureConfiguration(
    val packageName: String,
    val buildConfiguration: BuildConfiguration = BuildConfiguration(),
    val testInstrumentationRunnerClass: String = androidJunitRunner,
    val consumerMinificationFiles: Set<String> = emptySet(),
    val manifestPlaceholders: Map<String, Any> = emptyMap(),
    val viewBinding: Boolean = false,
    val dataBinding: Boolean = false,
    val selfValidator: Validator = validator(LibraryTargetTemplate)
)

fun androidLibraryFeatureDefinition(
    featureConfiguration: AndroidLibraryFeatureConfiguration
) = FeatureDefinition(
    pluginName = "com.android.library",
    pluginExtension = LibraryExtension::class,
    featureConfiguration = featureConfiguration,
    configuration = { extension, feature, project, formaConfiguration ->
        with(extension) {
            if (formaConfiguration.generateMissedManifests) {
                maybeGenerateManifest(project, feature)
            }
            if (formaConfiguration.validateManifestPackages) {
                validateManifestPackage(feature.packageName, project)
            }
            compileSdkVersion(formaConfiguration.compileSdk)

            defaultConfig.applyFrom(
                formaConfiguration,
                feature.testInstrumentationRunnerClass,
                feature.consumerMinificationFiles,
                feature.manifestPlaceholders
            )

            sourceSets["main"].java.srcDirs("src/main/kotlin")
            sourceSets["test"].java.srcDirs("src/test/kotlin")
            sourceSets["androidTest"].java.srcDirs("src/androidTest/kotlin")

            buildTypes.applyFrom(feature.buildConfiguration)
            compileOptions.applyFrom(formaConfiguration)

            buildFeatures.dataBinding = feature.dataBinding
            buildFeatures.viewBinding = feature.viewBinding
        }
    }
)

private fun LibraryExtension.validateManifestPackage(
    packageName: String,
    project: Project
) {
    sourceSets.forEach {
        if (it.manifest.srcFile.exists()) {
            val manifestPackageName = DocumentBuilderFactory
                .newInstance()
                .newDocumentBuilder()
                .parse(InputSource(it.manifest.srcFile.reader()))
                .documentElement
                .getAttribute("package")

            if (manifestPackageName != packageName) {
                error(
                    "Manifest package != build.gradle.kts packageName, it must be equal" +
                            "\nmanifest ${it.manifest.srcFile.absolutePath}" +
                            "\npackage $manifestPackageName" +
                            "\nbuild script ${project.buildFile.absolutePath}" +
                            "\npackageName $packageName"
                )
            }
        }
    }
}

private fun LibraryExtension.maybeGenerateManifest(
    project: Project,
    feature: AndroidLibraryFeatureConfiguration
) {
    val srcDir = File(project.projectDir, "src")
    val mainDir = File(srcDir, "main")
    // other source sets inherit manifest in case of existing main manifest
    if (mainDir.isDirectory && mainDir.exists()) {
        populateManifest(File(mainDir, "AndroidManifest.xml"), feature.packageName)
    } else {
        val sourceSetNames = sourceSets.names
        srcDir
            .listFilesOrdered { it.name in sourceSetNames && it.isDirectory }
            .forEach {
                populateManifest(File(it, "AndroidManifest.xml"), feature.packageName)
            }
    }
}

/**
 * Manifest file generated and added it to sourceSet
 * @param buildDir - project's build dir
 * @param packageName - will be injected to manifest template
 * @return path to generated file
 */
private fun populateManifest(
    manifestFile: File,
    packageName: String
): String {
    /**
     * Some naive caching here, file name equals package name
     * We create new file only if package is changed or on first build
     */
    with(manifestFile) {
        if (!exists()) {
            parentFile.mkdirs()
            createNewFile()
            writeText(
                "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                        "<manifest package=\"$packageName\"/>"
            )
        }
    }
    return manifestFile.path
}