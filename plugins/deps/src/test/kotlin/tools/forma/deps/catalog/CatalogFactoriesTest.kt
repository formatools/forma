package tools.forma.deps.catalog

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

/**
 * Catalog factory happy path (no Settings / VersionCatalogBuilder Gradle runtime).
 */
class CatalogFactoriesTest {

    @Test
    fun `library factory keeps explicit name and default generator`() {
        val named = library("io.coil-kt:coil:2.1.0", name = "coil")
        assertEquals("io.coil-kt:coil:2.1.0", named.groupArtifactVersion)
        assertEquals("coil", named.name)

        val auto = library("com.jakewharton.timber:timber:5.0.1")
        assertNull(auto.name)
        assertEquals("jakewhartonTimber", auto.nameGenerator(auto.groupArtifactVersion))
    }

    @Test
    fun `bundle factory keeps member coordinates`() {
        val b =
            bundle(
                name = "room",
                "androidx.room:room-runtime:2.5.1",
                "androidx.room:room-ktx:2.5.1",
            )
        assertEquals("room", b.name)
        assertEquals(2, b.groupArtifactVersions.size)
        assertEquals(
            "roomRuntime",
            b.nameGenerator(b.groupArtifactVersions[0] as String)
        )
    }

    @Test
    fun `plugin factory keeps id version configuration and companion deps`() {
        val p =
            plugin(
                id = "com.google.devtools.ksp:symbol-processing-gradle-plugin",
                version = "2.0.21-1.0.28",
                configuration = tools.forma.deps.core.CustomConfiguration("ksp"),
                "androidx.room:room-compiler:2.5.1",
            )
        assertEquals("com.google.devtools.ksp:symbol-processing-gradle-plugin", p.id)
        assertEquals("2.0.21-1.0.28", p.version)
        assertEquals("ksp", p.configuration?.name)
        assertEquals(1, p.dependencies.size)
        assertEquals(
            "devtoolsKspSymbolProcessing",
            p.nameGenerator(p.id)
        )
    }

    @Test
    fun `generateName rejects blank after filtering`() {
        assertFailsWith<IllegalArgumentException> {
            generateName(
                tokens = listOf("com", "android", "plugin"),
                source = "com.android.plugin",
                kind = "plugin",
            )
        }
    }

    @Test
    fun `defaultNameGenerator keeps distinct non-filtered tokens`() {
        assertEquals(
            "squareupOkhttp3Okhttp",
            defaultNameGenerator("com.squareup.okhttp3:okhttp:4.12.0")
        )
    }
}
