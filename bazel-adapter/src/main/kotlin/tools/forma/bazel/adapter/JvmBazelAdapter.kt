package tools.forma.bazel.adapter

import tools.forma.bazel.model.DepRef
import tools.forma.bazel.model.FormaProjectModel
import tools.forma.bazel.model.TargetSnapshot
import tools.forma.core.restriction.RestrictionGraph
import tools.forma.core.target.DefaultTargetRegistry
import tools.forma.core.target.SimpleTargetType
import tools.forma.core.target.TargetRegistry
import tools.forma.core.target.TargetType

/**
 * JVM-first Bazel adapter implementation (F-041 spike).
 *
 * - Re-registers the exact six JVM types + matrix from JvmTargetRegistry using ONLY core APIs.
 *   (See JvmTargetRegistry.kt and docs/JVM-TARGETS.md; keep in sync on matrix changes.)
 * - Does NOT depend on :jvm plugin module (avoids Kotlin GP / AGP transitive).
 * - Uses RestrictionGraph.isAllowed to filter/generate legal deps and to drive check().
 * - Never emits impl → impl (or other illegal) edges.
 * - Labels follow §3 conventions.
 * - v3 emit: Starlark macros (`jvm_api` / `jvm_impl` / …) so BUILD files stay attrs-only.
 *   Rule kind, tags, srcs glob, and matrix live in `//forma:defs.bzl`.
 *
 * Visibility policy (hybrid, sufficient for spike):
 * - jvm.binary → omit visibility (macro default `//visibility:public`)
 * - Others → narrow list of //consumer-pkg:__pkg__ for actual declared consumers in the model
 *   (computed via reverse lookup + graph allows).
 * - api targets also grant their direct impl + the binary.
 */
class JvmBazelAdapter : FormaToBazel {

    // --- JVM type registry recreated from core only (duplicate ids/suffixes; comment is source link) ---
    // Declare type map FIRST so init order is safe when registry registration runs.
    private val typeById: Map<String, TargetType> = buildMap {
        // The six authoritative JVM ids (must match plugins/jvm/.../JvmTargetTypes.kt)
        put("jvm.api", type("jvm.api", "api"))
        put("jvm.impl", type("jvm.impl", "impl"))
        put("jvm.library", type("jvm.library", "library"))
        put("jvm.util", type("jvm.util", "util"))
        put("jvm.test-util", type("jvm.test-util", "test-util"))
        put("jvm.binary", type("jvm.binary", "binary"))
    }

    private fun type(id: String, suffix: String): TargetType = SimpleTargetType(id, suffix)

    private val registry: TargetRegistry = DefaultTargetRegistry().also { reg ->
        registerJvmMatrix(reg)
    }
    private val graph: RestrictionGraph = registry.restrictionGraph()

    /**
     * Exact matrix copy (implementation edges). Source of truth comment:
     * plugins/jvm/src/main/java/tools/forma/jvm/target/JvmTargetRegistry.kt : registerJvmDefaults
     */
    private fun registerJvmMatrix(reg: TargetRegistry) {
        val t = typeById
        fun regType(id: String, allowed: Set<String>) {
            val tt = t.getValue(id)
            val allowedTypes = allowed.map { t.getValue(it) }.toSet()
            reg.register(
                tools.forma.core.target.TargetRegistration(
                    type = tt,
                    allowedDependencies = allowedTypes
                )
            )
        }

        // api: contracts only
        regType("jvm.api", setOf("jvm.api", "jvm.library"))
        // impl: api + library + util + test-util ; NO impl
        regType("jvm.impl", setOf("jvm.api", "jvm.library", "jvm.util", "jvm.test-util"))
        // library
        regType("jvm.library", setOf("jvm.util", "jvm.test-util"))
        // util
        regType("jvm.util", setOf("jvm.util", "jvm.library"))
        // test-util
        regType("jvm.test-util", setOf("jvm.test-util", "jvm.util", "jvm.library"))
        // binary: composition root
        regType("jvm.binary", setOf("jvm.api", "jvm.impl", "jvm.library", "jvm.util", "jvm.test-util"))
    }

    // --- label / path logic per BAZEL-ADAPTER §3 ---

    data class BazelCoords(val packageDir: String, val targetName: String) {
        val label: String get() = "//$packageDir:$targetName"
        val pkgVisibility: String get() = "//$packageDir:__pkg__"
    }

    fun starlarkMacro(typeId: String): String = when (typeId) {
        "jvm.api" -> "jvm_api"
        "jvm.impl" -> "jvm_impl"
        "jvm.library" -> "jvm_library"
        "jvm.util" -> "jvm_util"
        "jvm.test-util" -> "jvm_test_util"
        "jvm.binary" -> "jvm_binary"
        else -> error("No Starlark macro for typeId $typeId")
    }

    fun toBazelCoords(gradlePath: String): BazelCoords {
        val cleaned = gradlePath.trim().removePrefix(":").ifBlank { gradlePath }
        val segments = cleaned.split(':').filter { it.isNotBlank() }
        val packageDir = segments.joinToString("/")
        val targetName = segments.lastOrNull() ?: "root"
        return BazelCoords(packageDir, targetName)
    }

    private fun toLabel(gradlePath: String): String = toBazelCoords(gradlePath).label

    // --- generate ---

    override fun generate(model: FormaProjectModel): Map<String, String> {
        val typeOf = model.targets.associate { it.gradlePath to (typeById[it.typeId] ?: error("Unknown typeId ${it.typeId}")) }

        // Build reverse consumers for visibility (only actual declared edges that are legal)
        val consumersOf: MutableMap<String, MutableSet<String>> = mutableMapOf() // producerPath -> set of consumerPaths
        for (consumer in model.targets) {
            val cType = typeOf[consumer.gradlePath] ?: continue
            for (dep in consumer.dependencies) {
                val producerPath = dep.path
                val pType = typeOf[producerPath]
                if (pType != null && graph.isAllowed(cType, pType)) {
                    consumersOf.getOrPut(producerPath) { mutableSetOf() }.add(consumer.gradlePath)
                }
            }
        }

        val out = mutableMapOf<String, String>()

        for (target in model.targets) {
            val coords = toBazelCoords(target.gradlePath)
            val tType = typeOf[target.gradlePath] ?: error("Missing type for ${target.gradlePath}")
            val isBinary = target.typeId == "jvm.binary"
            val macro = starlarkMacro(target.typeId)

            val allowedDeps = target.dependencies.filter { d ->
                val pType = typeOf[d.path]
                pType != null && graph.isAllowed(tType, pType)
            }

            val depLabels = allowedDeps.map { d -> toLabel(d.path) }.sorted()

            val visibility = if (isBinary) {
                // Type-owned default is //visibility:public — omit unless metadata overrides.
                emptyList()
            } else {
                val cons = consumersOf[target.gradlePath] ?: emptySet()
                if (cons.isEmpty()) {
                    // No declared consumer in model → keep narrow (empty list means default private in Bazel)
                    emptyList()
                } else {
                    cons.map { c -> toBazelCoords(c).pkgVisibility }.sorted()
                }
            }

            val mainClass = target.metadata["mainClass"]

            val content = buildString {
                appendLine("# GENERATED by JvmBazelAdapter (v3 Starlark macros) — attrs only.")
                appendLine("# gradlePath=${target.gradlePath} type=${target.typeId}")
                appendLine("# Type owns rule/tags/srcs/matrix via //forma:defs.bzl")
                appendLine()
                appendLine("load(\"//forma:defs.bzl\", \"$macro\")")
                appendLine()
                appendLine("$macro(")
                appendLine("    name = \"${coords.targetName}\",")

                if (depLabels.isNotEmpty()) {
                    appendLine("    deps = [")
                    depLabels.forEach { appendLine("        \"$it\"," ) }
                    appendLine("    ],")
                }

                if (visibility.isNotEmpty()) {
                    appendLine("    visibility = [")
                    visibility.forEach { appendLine("        \"$it\"," ) }
                    appendLine("    ],")
                }

                if (mainClass != null) {
                    appendLine("    main_class = \"$mainClass\",")
                }

                appendLine(")")
            }

            out[coords.packageDir] = content.trimEnd() + "\n"
        }

        return out
    }

    // --- check ---

    override fun check(model: FormaProjectModel, existingBuilds: Map<String, String>): CheckReport {
        val violations = mutableListOf<String>()
        val warnings = mutableListOf<String>()

        val typeOf = model.targets.associate { it.gradlePath to (typeById[it.typeId] ?: error("Unknown typeId ${it.typeId} for check")) }

        for (consumer in model.targets) {
            val cType = typeOf[consumer.gradlePath] ?: continue
            for (dep in consumer.dependencies) {
                val pPath = dep.path
                val pType = typeOf[pPath]
                if (pType == null) {
                    warnings += "Unknown dep target '$pPath' referenced by ${consumer.gradlePath}"
                    continue
                }
                if (!graph.isAllowed(cType, pType)) {
                    violations += "Illegal dependency: ${consumer.typeId} (${consumer.gradlePath}) → ${pType.id} ($pPath)"
                }
            }
            // test deps not validated strictly in spike (future EdgeKind.TEST)
        }

        // Optional: very light sanity on existingBuilds if provided (look for forma:type tags + obvious bad strings)
        for ((pkg, buildText) in existingBuilds) {
            if ("impl" in pkg && "jvm.impl" in buildText) {
                // naive: look for other impl labels in deps
                if (Regex("""//[^:]+/impl:impl""").containsMatchIn(buildText)) {
                    warnings += "Possible cross-impl dep string found in generated $pkg/BUILD"
                }
            }
        }

        return CheckReport(violations = violations.sorted(), warnings = warnings.sorted())
    }
}
