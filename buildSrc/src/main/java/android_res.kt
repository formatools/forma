import org.gradle.api.Project

enum class ResType {
    string,
    layout, // TODO maybe separate fun
    xml,
    color,
    anim,
    drawable,
    menu,
    style,
    font,
    bool,
    dimen,
    integer,
    other
}

fun Project.resources(type: ResType): Unit = TODO()