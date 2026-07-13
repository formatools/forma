plugins {
    kotlin("jvm")
    `maven-publish`
}

// Library (not a Gradle plugin). Same group/version as plugins for mavenLocal + Portal later.
group = rootProject.group
version = rootProject.version

java {
    withSourcesJar()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            // Explicit coordinates so consumers resolve tools.forma:core:<version>
            groupId = project.group.toString()
            artifactId = "core"
            version = project.version.toString()

            pom {
                name.set("forma-core")
                description.set(
                    "Forma core library (not a Gradle plugin): TargetType, RestrictionGraph, " +
                        "TargetValidator / ContentRule, TargetRegistry. Co-versioned with tools.forma.* plugins."
                )
                url.set("https://forma.tools/")
                scm {
                    connection.set("scm:git:git://github.com/formatools/forma.git")
                    developerConnection.set("scm:git:ssh://github.com/formatools/forma.git")
                    url.set("https://github.com/formatools/forma")
                }
            }
        }
    }
}
