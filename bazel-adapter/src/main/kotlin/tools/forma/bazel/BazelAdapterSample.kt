package tools.forma.bazel

import tools.forma.bazel.adapter.JvmBazelAdapter
import tools.forma.bazel.sample.JvmApplicationFixture

/**
 * Tiny driver for humans:
 *   cd bazel-adapter
 *   ../gradlew runSample   (or ./gradlew after wrapper)
 *
 * Prints the generated BUILD.bazel fragments for the jvm-application fixture.
 */
fun main() {
    val adapter = JvmBazelAdapter()
    val model = JvmApplicationFixture.model
    val builds = adapter.generate(model)

    println("=== F-041 Bazel adapter sample generate for jvm-application fixture ===")
    println("Workspace: ${model.workspaceName}")
    println("Targets: ${model.targets.size}")
    println()

    builds.toSortedMap().forEach { (pkg, content) ->
        println("----- $pkg/BUILD.bazel -----")
        println(content)
        println()
    }

    val report = adapter.check(model)
    println("=== check(fixture) ===")
    println("violations=${report.violations.size} warnings=${report.warnings.size}")
    report.violations.forEach { println("VIOL: $it") }
    report.warnings.forEach { println("WARN: $it") }
}
