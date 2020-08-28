val kotlin_version = "1.4.0"
val agp_version = "4.1.0-rc01"

object kotlinx {
    val coroutines_core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.9"
    val coroutines_android = dependencies(
        coroutines_core,
        "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.9"
    )
}

object androidx {
    val annotation = "androidx.annotation:annotation:1.1.0"
    val collection = dependencies(
        annotation,
        "androidx.collection:collection:1.0.0"
    )
    val versionedparcelable = dependencies(
        dependencies(
            "androidx.versionedparcelable:versionedparcelable:1.1.0",
            annotation
        ),
        collection
    )
    val cardview = dependencies(
        annotation,
        "androidx.cardview:cardview:1.0.0"
    )
    val core_common = dependencies(
        annotation,
        "androidx.arch.core:core-common:1.3.1"
    )
    val lifecycle_common = dependencies(
        annotation,
        "androidx.lifecycle:lifecycle-common:2.2.0"
    )
    val lifecycle_runtime = dependencies(
        dependencies("androidx.lifecycle:lifecycle-runtime:2.2.0"),
        core_common,
        lifecycle_common
    )
    val lifecycle_viewmodel = dependencies(
        annotation,
        "androidx.lifecycle:lifecycle-viewmodel:2.2.0"
    )

    val lifecycle_viewmodel_ktx = dependencies(
        dependencies("androidx.lifecycle:lifecycle-viewmodel-ktx:2.2.0"),
        lifecycle_viewmodel,
        kotlinx.coroutines_android
    )

    val lifecycle_runtime_ktx = dependencies(
        dependencies(
            "androidx.lifecycle:lifecycle-runtime-ktx:2.2.0",
            annotation
        ),
        kotlinx.coroutines_android
    )

    val savedstate = dependencies(
        dependencies(
            "androidx.savedstate:savedstate:1.0.0",
            annotation
        ),
        core_common,
        lifecycle_common
    )

    val core_runtime = dependencies(
        dependencies(
            annotation,
            "androidx.arch.core:core-runtime:2.1.0"
        ),
        core_common
    )

    val lifecycle_livedate_core = dependencies(
        dependencies("androidx.lifecycle:lifecycle-livedata-core:2.2.0"),
        core_common,
        core_runtime
    )

    val lifecycle_viewmodel_savedstate = dependencies(
        dependencies(
            "androidx.lifecycle:lifecycle-viewmodel-savedstate:2.2.0",
            annotation
        ),
        lifecycle_livedate_core,
        lifecycle_viewmodel,
        savedstate
    )

    val core = dependencies(
        dependencies(
            annotation,
            "androidx.core:core:1.3.1"
        ),
        lifecycle_runtime,
        versionedparcelable
    )

    val core_ktx = dependencies(
        dependencies(annotation),
        core
    )

    val activity = dependencies(
        dependencies(
            "androidx.activity:activity:1.1.0",
            annotation
        ),
        core,
        lifecycle_runtime,
        lifecycle_viewmodel,
        lifecycle_viewmodel_savedstate,
        savedstate
    )

    val loader = dependencies(
        dependencies(
            "androidx.loader:loader:1.1.0",
            annotation
        ),
        collection,
        core,
        lifecycle_livedate_core,
        lifecycle_viewmodel
    )

    val customview = dependencies(
        dependencies(
            "androidx.customview:customview:1.1.0",
            annotation
        ),
        core
    )

    val viewpager = dependencies(
        dependencies(
            "androidx.viewpager:viewpager:1.0.0",
            annotation
        ),
        core,
        customview
    )

    val fragment = dependencies(
        dependencies(
            "androidx.fragment:fragment:1.2.5",
            annotation
        ),
        activity,
        collection,
        core,
        lifecycle_livedate_core,
        lifecycle_viewmodel,
        lifecycle_viewmodel_savedstate,
        loader,
        viewpager
    )

    val activity_ktx = dependencies(
        dependencies("androidx.activity:activity-ktx:1.1.0"),
        activity,
        core_ktx,
        lifecycle_runtime_ktx,
        lifecycle_viewmodel_ktx
    )

    val legacy_ui = "androidx.legacy:legacy-support-core-ui:1.0.0"
    val legacy_utils = "androidx.legacy:legacy-support-core-utils:1.0.0"
    val recyclerview = "androidx.recyclerview:recyclerview:1.1.0"
    val transition = "androidx.transition:transition:1.3.1"
    val cursoradapter = "androidx.cursoradapter:cursoradapter:1.0.0"
    val drawerlayout = "androidx.drawerlayout:drawerlayout:1.1.0"
    val navigation_common = "androidx.navigation:navigation-common:2.3.0"

    val appcompat = dependencies(
        dependencies(
            "androidx.appcompat:appcompat:1.2.0",
            "androidx.appcompat:appcompat-resources:1.2.0",
            annotation,
            cursoradapter,
            drawerlayout
        ),
        fragment
    )
    val constraintlayout = dependencies(
        dependencies(
            "androidx.constraintlayout:constraintlayout:2.0.1",
            "androidx.constraintlayout:constraintlayout-solver:2.0.1"
        ),
        appcompat,
        core
    )

    val navigation_ui_ktx = dependencies(
        "androidx.navigation:navigation-ui-ktx:2.3.0",
        "androidx.navigation:navigation-ui:2.3.0"
    )

    val navigation_common_ktx = dependencies(
        "androidx.navigation:navigation-common:2.3.0"
    )

    val navigation_runtime = dependencies(
        dependencies(
            "androidx.navigation:navigation-runtime:2.3.0",
            navigation_common
        ),
        activity,
        lifecycle_viewmodel,
        lifecycle_viewmodel_savedstate,
        savedstate
    )

    val navigation_runtime_ktx = dependencies(
        navigation_runtime,
        navigation_common_ktx,
        dependencies(
            activity_ktx,
            lifecycle_viewmodel_ktx
        )
    )

    val navigation_fragment_ktx = dependencies(
        navigation_runtime,
        fragment,
        dependencies(
            "androidx.navigation:navigation-fragment:2.3.0",
            "androidx.navigation:navigation-fragment-ktx:2.3.0"
        )
    )
}

object google {
    val material = dependencies(
        androidx.appcompat,
        androidx.cardview,
        androidx.core,
        dependencies(
            androidx.annotation,
            androidx.legacy_ui,
            androidx.legacy_utils,
            androidx.recyclerview,
            androidx.transition
        )
    )
}