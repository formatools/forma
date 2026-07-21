package tools.forma.core.fleet

import java.nio.file.Files
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class PackageLayoutTest {

    @Test
    fun `segments splits dotted package`() {
        assertEquals(
            listOf("com", "stepango", "blockme", "character", "list", "api"),
            PackageLayout.segments("com.stepango.blockme.character.list.api"),
        )
    }

    @Test
    fun `sourceDir kotlin default matches GH-54 layout`() {
        val expected =
            "src/main/kotlin/com/stepango/blockme/character/list/api"
        assertEquals(
            expected,
            PackageLayout.sourceDir("com.stepango.blockme.character.list.api"),
        )
    }

    @Test
    fun `sourceDir java language`() {
        assertEquals(
            "src/main/java/com/foo/bar",
            PackageLayout.sourceDir("com.foo.bar", SourceLanguage.JAVA),
        )
    }

    @Test
    fun `empty package rejected`() {
        assertFailsWith<IllegalArgumentException> { PackageLayout.segments("") }
        assertFailsWith<IllegalArgumentException> { PackageLayout.segments("   ") }
    }

    @Test
    fun `empty segment rejected`() {
        assertFailsWith<IllegalArgumentException> { PackageLayout.segments("com..foo") }
    }

    @Test
    fun `invalid segment rejected`() {
        assertFailsWith<IllegalArgumentException> { PackageLayout.segments("com.foo-bar.baz") }
        assertFailsWith<IllegalArgumentException> { PackageLayout.segments("com.1foo") }
    }
}

class ProjectPathFormsTest {

    @Test
    fun `gradle and forma path forms`() {
        assertEquals(":feature-home-impl", ProjectPathForms.gradleProjectPath("feature/home/impl"))
        assertEquals(":feature:home:impl", ProjectPathForms.formaTargetPath("feature/home/impl"))
    }

    @Test
    fun `normalizes slashes and dots`() {
        assertEquals(":feature-home-api", ProjectPathForms.gradleProjectPath("./feature/home/api/"))
        assertEquals(":a:b", ProjectPathForms.formaTargetPath("\\a\\b"))
    }

    @Test
    fun `fromGradleProjectPath best effort reverse`() {
        assertEquals(
            "feature/home/impl",
            ProjectPathForms.fromGradleProjectPath(":feature-home-impl"),
        )
    }

    @Test
    fun `empty relative rejected`() {
        assertFailsWith<IllegalArgumentException> { ProjectPathForms.gradleProjectPath("") }
    }
}

class LayoutGeneratorCheckerTest {

    @Test
    fun `apply creates package dirs idempotently`() {
        val root = Files.createTempDirectory("forma-fleet-")
        try {
            val pkg = "com.stepango.blockme.character.list.api"
            val req =
                GenerateLayoutRequest(
                    moduleDir = root,
                    packageName = pkg,
                    language = SourceLanguage.KOTLIN,
                    createPlaceholder = true,
                )
            val first = LayoutGenerator.apply(req)
            assertTrue(first.createdDirs.isNotEmpty())
            assertTrue(first.createdFiles.any { it.fileName.toString() == ".gitkeep" })
            assertTrue(LayoutChecker.checkPackageSourceDir(root, pkg).isEmpty())

            val second = LayoutGenerator.apply(req)
            assertTrue(second.createdDirs.isEmpty())
            assertTrue(second.alreadyExisted.isNotEmpty())
            assertTrue(LayoutChecker.checkPackageSourceDir(root, pkg).isEmpty())
        } finally {
            root.toFile().deleteRecursively()
        }
    }

    @Test
    fun `check reports missing package dir`() {
        val root = Files.createTempDirectory("forma-fleet-miss-")
        try {
            val v = LayoutChecker.checkPackageSourceDir(root, "com.foo.bar")
            assertEquals(1, v.size)
            assertTrue(v.single().path.contains("src/main/kotlin/com/foo/bar"))
        } finally {
            root.toFile().deleteRecursively()
        }
    }

    @Test
    fun `plan lists package path without IO side effects`() {
        val root = Files.createTempDirectory("forma-fleet-plan-")
        try {
            val plan =
                LayoutGenerator.plan(
                    GenerateLayoutRequest(root, "com.foo.bar", createPlaceholder = true),
                )
            assertTrue(plan.createdDirs.isNotEmpty())
            assertTrue(plan.createdFiles.single().endsWith(".gitkeep"))
            assertTrue(Files.notExists(root.resolve("src")))
        } finally {
            root.toFile().deleteRecursively()
        }
    }
}

class MigratePlannerTest {

    @Test
    fun `planRename emits path and reference rewrites`() {
        val plan =
            MigratePlanner.planRename(
                PathRenamePlan(
                    fromModuleRelative = "feature/home/impl",
                    toModuleRelative = "feature/dashboard/impl",
                ),
            )
        assertEquals(
            listOf("feature/home/impl" to "feature/dashboard/impl"),
            plan.filesystemMoves,
        )
        assertTrue(plan.referenceRewrites.contains(":feature-home-impl" to ":feature-dashboard-impl"))
        assertTrue(plan.referenceRewrites.contains(":feature:home:impl" to ":feature:dashboard:impl"))
        assertTrue(
            plan.referenceRewrites.any {
                it.first.contains("target(") && it.second.contains("dashboard")
            },
        )
        assertTrue(plan.notes.isNotEmpty())
    }

    @Test
    fun `same path rejected`() {
        assertFailsWith<IllegalArgumentException> {
            MigratePlanner.planRename(PathRenamePlan("a/api", "a/api"))
        }
    }
}
