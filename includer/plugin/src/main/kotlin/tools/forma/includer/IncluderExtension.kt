package tools.forma.includer

abstract class IncluderExtension {

    /**
     * Folder names to be excluded when searching for submodules.
     *
     * Default set it: `build`, `src`, `buildSrc`
     */
    var excludeFolders: Set<String> = DEFAULT_EXCLUDED_FOLDERS

    /**
     * If true, include directories with any `.gradle(.kts)` files as modules.
     *
     * If false, only include directories with `build.gradle(.kts)` files as modules.
     */
    var arbitraryBuildScriptNames: Boolean = false

    /** Add folder names to be excluded when searching for submodules */
    fun excludeFolders(vararg names: String) {
        excludeFolders = excludeFolders + names
    }

    private companion object {
        private val DEFAULT_EXCLUDED_FOLDERS = setOf("build", "src", "buildSrc")
    }
}
