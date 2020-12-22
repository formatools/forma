package tools.forma.target

import org.gradle.api.Project

class FormaTarget(val project: Project) {
    val name: String = project.name
}