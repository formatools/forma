package tools.forma.deps.fleet

import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.TaskProvider
import tools.forma.config.FormaSettingsStore
import tools.forma.core.fleet.GenerateLayoutRequest
import tools.forma.core.fleet.LayoutChecker
import tools.forma.core.fleet.LayoutGenerator
import tools.forma.core.fleet.PackageLayout
import tools.forma.core.fleet.SourceLanguage
import java.io.File
import java.nio.file.Files

const val FORMA_LAYOUT_CHECK_TASK = "formaLayoutCheck"
const val FORMA_LAYOUT_GENERATE_TASK = "formaLayoutGenerate"
const val FORMA_LAYOUT_CHECK_ALL_TASK = "formaLayoutCheckAll"
const val FORMA_LAYOUT_GENERATE_ALL_TASK = "formaLayoutGenerateAll"

private const val LAYOUT_GROUP = "forma layout"
private const val ROOT_AGGREGATE_MARKER = "forma.layout.rootTasksRegistered"
private const val PLACEHOLDER_PROPERTY = "forma.layout.createPlaceholder"

/**
 * One global way to record `packageName` layout intent and expose thin Gradle shells
 * over [tools.forma.core.fleet] APIs (F-088 / F-084 follow-up).
 *
 * Idempotent: safe if a target DSL calls this more than once on the same project.
 *
 * Registers:
 * - extension [FormaLayoutExtension] (`formaLayout`)
 * - [FORMA_LAYOUT_CHECK_TASK] / [FORMA_LAYOUT_GENERATE_TASK] on this project
 * - root [FORMA_LAYOUT_CHECK_ALL_TASK] / [FORMA_LAYOUT_GENERATE_ALL_TASK] (once)
 *
 * Optional configuration-time failure when
 * [tools.forma.config.AndroidProjectSettings.checkPackageLayoutAtConfiguration] is true
 * and Forma Android settings are stored (pure JVM skips the hook).
 *
 * Check accepts either `src/main/kotlin` or `src/main/java` package trees (sample and
 * many Android trees use the java root for Kotlin sources). Generate prefers an
 * existing conventional root, else [language] (default KOTLIN).
 */
fun Project.registerFormaLayout(
    packageName: String,
    language: SourceLanguage = SourceLanguage.KOTLIN,
    /**
     * When false, check is a no-op success (AGP namespace-only targets: res, viewBinding,
     * androidBinary). Generate still scaffolds a package tree if invoked explicitly.
     */
    requirePackageSourceDir: Boolean = true,
) {
    val existing = extensions.findByType(FormaLayoutExtension::class.java)
    if (existing != null) {
        // Already registered — keep first metadata; tasks already wired.
        return
    }

    val extension =
        extensions.create("formaLayout", FormaLayoutExtension::class.java).apply {
            this.packageName = packageName
            this.language = language
            this.requirePackageSourceDir = requirePackageSourceDir
        }

    // Capture serializable values only (configuration-cache safe task actions).
    val moduleDir: File = projectDir
    val pkg: String = packageName
    val preferredLanguage: SourceLanguage = language
    val requireSources: Boolean = requirePackageSourceDir
    val createPlaceholder: Boolean =
        findProperty(PLACEHOLDER_PROPERTY)?.toString().let { raw ->
            when {
                raw == null -> false
                raw.equals("true", ignoreCase = true) || raw == "1" -> true
                else -> false
            }
        }

    val checkTask = registerLayoutCheckTask(moduleDir, pkg, requireSources)
    val generateTask =
        registerLayoutGenerateTask(
            moduleDir = moduleDir,
            packageName = pkg,
            preferredLanguage = preferredLanguage,
            createPlaceholder = createPlaceholder,
            requirePackageSourceDir = requireSources,
        )
    ensureRootLayoutAggregateTasks(checkTask, generateTask)

    if (requireSources) {
        maybeFailAtConfigurationIfPackageDirMissing(moduleDir, pkg)
    }
}

/**
 * Ensures root aggregate tasks exist (e.g. from androidProjectConfiguration before any
 * target registers). No-op if already registered.
 */
fun Project.ensureFormaLayoutRootTasks() {
    val root = rootProject
    if (root.extensions.extraProperties.has(ROOT_AGGREGATE_MARKER)) return
    root.extensions.extraProperties.set(ROOT_AGGREGATE_MARKER, true)
    root.tasks.register(FORMA_LAYOUT_CHECK_ALL_TASK) { task ->
        task.group = LAYOUT_GROUP
        task.description =
            "Runs formaLayoutCheck on every subproject that registered package layout metadata"
    }
    root.tasks.register(FORMA_LAYOUT_GENERATE_ALL_TASK) { task ->
        task.group = LAYOUT_GROUP
        task.description =
            "Runs formaLayoutGenerate on every subproject that registered package layout metadata"
    }
}

private fun Project.registerLayoutCheckTask(
    moduleDir: File,
    packageName: String,
    requirePackageSourceDir: Boolean,
): TaskProvider<Task> {
    if (tasks.names.contains(FORMA_LAYOUT_CHECK_TASK)) {
        return tasks.named(FORMA_LAYOUT_CHECK_TASK)
    }
    return tasks.register(FORMA_LAYOUT_CHECK_TASK) { task ->
        task.group = LAYOUT_GROUP
        task.description =
            "Checks that src/main/{kotlin|java}/<packageName> exists (LayoutChecker)"
        task.inputs.property("packageName", packageName)
        task.inputs.property("moduleDir", moduleDir.path)
        task.inputs.property("requirePackageSourceDir", requirePackageSourceDir)
        task.doLast {
            if (!requirePackageSourceDir) {
                logger.lifecycle(
                    "formaLayoutCheck skipped (packageName is AGP identity only; " +
                        "no package source dir required) for '$packageName'",
                )
                return@doLast
            }
            val violations = checkPackageDirEitherLanguage(moduleDir, packageName)
            if (violations.isNotEmpty()) {
                val detail = violations.joinToString("\n") { "  $it" }
                throw GradleException(
                    "formaLayoutCheck failed for packageName='$packageName' " +
                        "under ${moduleDir.path}:\n$detail\n" +
                        "Scaffold with $FORMA_LAYOUT_GENERATE_TASK or root " +
                        "$FORMA_LAYOUT_GENERATE_ALL_TASK.",
                )
            }
            val found = existingPackageRel(moduleDir, packageName) ?: "(present)"
            logger.lifecycle("formaLayoutCheck OK: $found")
        }
    }
}

private fun Project.registerLayoutGenerateTask(
    moduleDir: File,
    packageName: String,
    preferredLanguage: SourceLanguage,
    createPlaceholder: Boolean,
    requirePackageSourceDir: Boolean,
): TaskProvider<Task> {
    if (tasks.names.contains(FORMA_LAYOUT_GENERATE_TASK)) {
        return tasks.named(FORMA_LAYOUT_GENERATE_TASK)
    }
    return tasks.register(FORMA_LAYOUT_GENERATE_TASK) { task ->
        task.group = LAYOUT_GROUP
        task.description =
            "Creates missing package source dirs from packageName (LayoutGenerator)"
        task.inputs.property("packageName", packageName)
        task.inputs.property("preferredLanguage", preferredLanguage.name)
        task.inputs.property("createPlaceholder", createPlaceholder)
        task.inputs.property("moduleDir", moduleDir.path)
        task.inputs.property("requirePackageSourceDir", requirePackageSourceDir)
        task.doLast {
            // AGP-identity-only targets: do not scaffold empty kotlin/java trees into res/binary.
            if (!requirePackageSourceDir) {
                logger.lifecycle(
                    "formaLayoutGenerate skipped (packageName is AGP identity only) " +
                        "for '$packageName'",
                )
                return@doLast
            }
            // Already present under either conventional root — no-op.
            if (existingPackageRel(moduleDir, packageName) != null) {
                logger.lifecycle(
                    "formaLayoutGenerate: already present for '$packageName'",
                )
                return@doLast
            }
            val language = resolveGenerateLanguage(moduleDir, preferredLanguage)
            val result =
                LayoutGenerator.apply(
                    GenerateLayoutRequest(
                        moduleDir = moduleDir.toPath(),
                        packageName = packageName,
                        language = language,
                        createPlaceholder = createPlaceholder,
                    ),
                )
            if (result.createdDirs.isEmpty() && result.createdFiles.isEmpty()) {
                logger.lifecycle(
                    "formaLayoutGenerate: already present for '$packageName'",
                )
            } else {
                result.createdDirs.forEach { dir ->
                    logger.lifecycle("formaLayoutGenerate: created dir $dir")
                }
                result.createdFiles.forEach { file ->
                    logger.lifecycle("formaLayoutGenerate: created file $file")
                }
            }
        }
    }
}

private fun Project.ensureRootLayoutAggregateTasks(
    checkTask: TaskProvider<Task>,
    generateTask: TaskProvider<Task>,
) {
    ensureFormaLayoutRootTasks()
    rootProject.tasks.named(FORMA_LAYOUT_CHECK_ALL_TASK).configure { it.dependsOn(checkTask) }
    rootProject.tasks.named(FORMA_LAYOUT_GENERATE_ALL_TASK).configure { it.dependsOn(generateTask) }
}

/**
 * Prefer an existing conventional source root so generate matches the module's style;
 * otherwise use the DSL preferred language (default KOTLIN).
 */
internal fun resolveGenerateLanguage(
    moduleDir: File,
    preferred: SourceLanguage,
): SourceLanguage {
    val javaRoot = moduleDir.resolve("src/main/java")
    val kotlinRoot = moduleDir.resolve("src/main/kotlin")
    return when {
        javaRoot.isDirectory && !kotlinRoot.isDirectory -> SourceLanguage.JAVA
        kotlinRoot.isDirectory && !javaRoot.isDirectory -> SourceLanguage.KOTLIN
        else -> preferred
    }
}

/** Relative package path if present under kotlin or java root; null if missing. */
internal fun existingPackageRel(moduleDir: File, packageName: String): String? {
    for (language in listOf(SourceLanguage.KOTLIN, SourceLanguage.JAVA)) {
        val rel = PackageLayout.sourceDir(packageName, language)
        if (Files.isDirectory(moduleDir.toPath().resolve(rel))) {
            return rel
        }
    }
    return null
}

/**
 * Empty list = OK (dir exists under kotlin and/or java). Otherwise human-readable
 * violation lines (not core LayoutCheckViolation — either-language policy is Gradle-shell).
 */
internal fun checkPackageDirEitherLanguage(
    moduleDir: File,
    packageName: String,
): List<String> {
    if (existingPackageRel(moduleDir, packageName) != null) return emptyList()
    // Surface invalid package via core checker.
    val probe =
        LayoutChecker.checkPackageSourceDir(
            moduleDir.toPath(),
            packageName,
            SourceLanguage.KOTLIN,
        )
    if (probe.any { it.path == packageName }) {
        return probe.map { it.message }
    }
    val kotlinRel = PackageLayout.sourceDir(packageName, SourceLanguage.KOTLIN)
    val javaRel = PackageLayout.sourceDir(packageName, SourceLanguage.JAVA)
    return listOf(
        "missing package source directory for '$packageName' " +
            "(expected $kotlinRel or $javaRel under module)",
    )
}

/**
 * Opt-in configuration-time strictness. Reads Android project settings when stored;
 * pure JVM (or unset store) skips without inventing a second configuration path.
 */
private fun Project.maybeFailAtConfigurationIfPackageDirMissing(
    moduleDir: File,
    packageName: String,
) {
    val enabled =
        try {
            FormaSettingsStore.settings.checkPackageLayoutAtConfiguration
        } catch (_: UninitializedPropertyAccessException) {
            false
        } catch (_: Throwable) {
            false
        }
    if (!enabled) return

    val violations = checkPackageDirEitherLanguage(moduleDir, packageName)
    if (violations.isEmpty()) return

    val detail = violations.joinToString("; ")
    logger.error(
        "Forma layout: missing package source dir for project '{}' packageName='{}'. " +
            "Enable is opt-in via androidProjectConfiguration(" +
            "checkPackageLayoutAtConfiguration = true). Scaffold with {} or {}. Detail: {}",
        path,
        packageName,
        FORMA_LAYOUT_GENERATE_TASK,
        FORMA_LAYOUT_GENERATE_ALL_TASK,
        detail,
    )
    throw GradleException(
        "checkPackageLayoutAtConfiguration: $detail " +
            "(project ${path}, packageName='$packageName')",
    )
}
