package tools.forma.core.fleet

import java.nio.file.Files
import java.nio.file.Path

data class GenerateLayoutRequest(
    val moduleDir: Path,
    val packageName: String,
    val language: SourceLanguage = SourceLanguage.KOTLIN,
    /** When true, write an empty `.gitkeep` under the package dir if no files exist there. */
    val createPlaceholder: Boolean = false,
)

data class GenerateLayoutResult(
    val createdDirs: List<Path>,
    val createdFiles: List<Path>,
    val alreadyExisted: List<Path>,
)

/**
 * Create (or plan) package source directories under a module root — GH #54 / F-084 generate mode.
 */
object LayoutGenerator {

    /** Pure plan: paths that would be created; does not touch the filesystem. */
    fun plan(request: GenerateLayoutRequest): GenerateLayoutResult {
        val packageDir = request.moduleDir.resolve(PackageLayout.sourceDir(request.packageName, request.language))
        val dirs = mutableListOf<Path>()
        var cursor = request.moduleDir
        val relative = request.moduleDir.relativize(packageDir)
        for (i in 0 until relative.nameCount) {
            cursor = cursor.resolve(relative.getName(i))
            dirs.add(cursor)
        }
        val files =
            if (request.createPlaceholder) {
                listOf(packageDir.resolve(".gitkeep"))
            } else {
                emptyList()
            }
        // plan mode cannot know existence without IO — report all as "to create"
        return GenerateLayoutResult(
            createdDirs = dirs,
            createdFiles = files,
            alreadyExisted = emptyList(),
        )
    }

    /** Create missing directories (and optional placeholder). Idempotent. */
    fun apply(request: GenerateLayoutRequest): GenerateLayoutResult {
        val packageRel = PackageLayout.sourceDir(request.packageName, request.language)
        val moduleRoot = request.moduleDir.toAbsolutePath().normalize()
        val packageDir = moduleRoot.resolve(packageRel).normalize()
        require(packageDir.startsWith(moduleRoot)) {
            "resolved package dir escapes moduleDir"
        }

        val createdDirs = mutableListOf<Path>()
        val alreadyExisted = mutableListOf<Path>()
        val createdFiles = mutableListOf<Path>()

        var cursor = moduleRoot
        val relative = moduleRoot.relativize(packageDir)
        for (i in 0 until relative.nameCount) {
            cursor = cursor.resolve(relative.getName(i))
            if (Files.isDirectory(cursor)) {
                alreadyExisted.add(cursor)
            } else {
                Files.createDirectories(cursor)
                createdDirs.add(cursor)
            }
        }

        if (request.createPlaceholder) {
            val gitkeep = packageDir.resolve(".gitkeep")
            if (Files.exists(gitkeep)) {
                alreadyExisted.add(gitkeep)
            } else {
                // only add placeholder if directory has no other entries
                val hasContent =
                    Files.list(packageDir).use { stream ->
                        stream.anyMatch { it.fileName.toString() != ".gitkeep" }
                    }
                if (!hasContent) {
                    Files.writeString(gitkeep, "")
                    createdFiles.add(gitkeep)
                }
            }
        }

        return GenerateLayoutResult(
            createdDirs = createdDirs,
            createdFiles = createdFiles,
            alreadyExisted = alreadyExisted,
        )
    }
}
