object viewbinding {
    val viewBinding = transitiveDeps(
        "androidx.databinding:viewbinding"
    )

    val viewpropertydelegate = deps(
        "com.github.kirich1409:viewbindingpropertydelegate-noreflection:${versions.viewbinding.viewbindingpropertydelegate}".dep,
        "com.github.kirich1409:viewbindingpropertydelegate-core:${versions.viewbinding.viewbindingpropertydelegate}".dep,
    )
}
