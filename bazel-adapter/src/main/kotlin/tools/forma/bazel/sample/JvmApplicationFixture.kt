package tools.forma.bazel.sample

import tools.forma.bazel.model.DepRef
import tools.forma.bazel.model.FormaProjectModel
import tools.forma.bazel.model.TargetSnapshot

/**
 * Hand-constructed model matching jvm-application/ layout and declared edges.
 * This is the source of truth for F-041 spike tests and golden output.
 *
 * Gradle paths and typeIds must match the real modules + JvmTargetTypes.
 * See jvm-application/{binary,feature,common}/**/build.gradle.kts
 */
object JvmApplicationFixture {

    val model: FormaProjectModel = FormaProjectModel(
        workspaceName = "jvm-application-spike",
        targets = listOf(
            // binary (composition root)
            TargetSnapshot(
                gradlePath = ":binary",
                typeId = "jvm.binary",
                packageName = "tools.forma.jvm.sample.binary",
                dependencies = listOf(
                    DepRef(":feature:greeter:api"),
                    DepRef(":feature:greeter:impl"),
                    DepRef(":feature:calculator:api"),
                    DepRef(":feature:calculator:impl"),
                    DepRef(":common:util"),
                    DepRef(":common:library"),
                ),
                metadata = mapOf("mainClass" to "tools.forma.jvm.sample.binary.MainKt")
            ),

            // greeter feature
            TargetSnapshot(
                gradlePath = ":feature:greeter:api",
                typeId = "jvm.api",
                packageName = "tools.forma.jvm.sample.feature.greeter.api",
                dependencies = emptyList()
            ),
            TargetSnapshot(
                gradlePath = ":feature:greeter:impl",
                typeId = "jvm.impl",
                packageName = "tools.forma.jvm.sample.feature.greeter.impl",
                dependencies = listOf(
                    DepRef(":feature:greeter:api"),
                    DepRef(":common:util"),
                    DepRef(":common:library"),
                ),
                testDependencies = listOf(DepRef(":common:test-util"))
            ),

            // calculator feature
            TargetSnapshot(
                gradlePath = ":feature:calculator:api",
                typeId = "jvm.api",
                packageName = "tools.forma.jvm.sample.feature.calculator.api",
                dependencies = emptyList()
            ),
            TargetSnapshot(
                gradlePath = ":feature:calculator:impl",
                typeId = "jvm.impl",
                packageName = "tools.forma.jvm.sample.feature.calculator.impl",
                dependencies = listOf(
                    DepRef(":feature:calculator:api"),
                    DepRef(":common:library"),
                    DepRef(":common:util"),
                )
            ),

            // common shared
            TargetSnapshot(
                gradlePath = ":common:library",
                typeId = "jvm.library",
                packageName = "tools.forma.jvm.sample.common.library",
                dependencies = emptyList()
            ),
            TargetSnapshot(
                gradlePath = ":common:util",
                typeId = "jvm.util",
                packageName = "tools.forma.jvm.sample.common.util",
                dependencies = emptyList()
            ),
            TargetSnapshot(
                gradlePath = ":common:test-util",
                typeId = "jvm.test-util",
                packageName = "tools.forma.jvm.sample.common.testutil",
                dependencies = emptyList()
            ),
        )
    )

    /** A deliberately illegal variant for check() tests: greeter impl also depends on calculator impl. */
    val modelWithIllegalImplToImpl: FormaProjectModel = model.copy(
        targets = model.targets.map { t ->
            if (t.gradlePath == ":feature:greeter:impl") {
                t.copy(
                    dependencies = t.dependencies + DepRef(":feature:calculator:impl")
                )
            } else t
        }
    )
}
