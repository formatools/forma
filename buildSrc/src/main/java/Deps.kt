val kotlin_version = "1.4.0"
val agp_version = "4.1.0-rc01"
val coroutines_version = "1.3.9"

object jetbrains {
    val annotations = "org.jetbrains:annotations:20.0.0".dep
}

object kotlin {
    val stdlib_common = "org.jetbrains.kotlin:kotlin-stdlib-common:$kotlin_version".dep
    val stdlib = dependencies(
        "org.jetbrains.kotlin:kotlin-stdlib:$kotlin_version".dep,
        jetbrains.annotations,
        stdlib_common
    )
    val stdlib_jdk7 = dependencies(
        "org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlin_version".dep,
        stdlib
    )
    val stdlib_jdk8 = dependencies(
        "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlin_version".dep,
        stdlib,
        stdlib_jdk7
    )
    val reflect = dependencies(
        "org.jetbrains.kotlin:kotlin-reflect:$kotlin_version".dep,
        stdlib
    )
}

object kotlinx {
    val coroutines_core = dependencies(
        "org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutines_version".dep,
        kotlin.stdlib
    )
    val coroutines_android = dependencies(
        "org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutines_version".dep,
        coroutines_core
    )
}

object androidx {
    val annotation = "androidx.annotation:annotation:1.1.0".dep
    val collection = dependencies(
        annotation,
        "androidx.collection:collection:1.0.0".dep
    )
    val versionedparcelable = dependencies(
        "androidx.versionedparcelable:versionedparcelable:1.1.0".dep,
        annotation,
        collection
    )
    val cardview = dependencies(
        "androidx.cardview:cardview:1.0.0".dep,
        annotation
    )
    val core_common = dependencies(
        annotation,
        "androidx.arch.core:core-common:2.1.0".dep
    )
    val lifecycle_common = dependencies(
        annotation,
        "androidx.lifecycle:lifecycle-common:2.2.0".dep
    )
    val lifecycle_runtime = dependencies(
        "androidx.lifecycle:lifecycle-runtime:2.2.0".dep,
        core_common,
        lifecycle_common
    )
    val lifecycle_viewmodel = dependencies(
        annotation,
        "androidx.lifecycle:lifecycle-viewmodel:2.2.0".dep
    )

    val lifecycle_viewmodel_ktx = dependencies(
        "androidx.lifecycle:lifecycle-viewmodel-ktx:2.2.0".dep,
        lifecycle_viewmodel,
        kotlinx.coroutines_android
    )

    val lifecycle_runtime_ktx = dependencies(
        "androidx.lifecycle:lifecycle-runtime-ktx:2.2.0".dep,
        annotation,
        kotlinx.coroutines_android
    )

    val savedstate = dependencies(
        "androidx.savedstate:savedstate:1.0.0".dep,
        annotation,
        core_common,
        lifecycle_common
    )

    val core_runtime = dependencies(
        "androidx.arch.core:core-runtime:2.1.0".dep,
        annotation,
        core_common
    )

    val lifecycle_livedate_core = dependencies(
        "androidx.lifecycle:lifecycle-livedata-core:2.2.0".dep,
        core_common,
        core_runtime
    )

    val lifecycle_viewmodel_savedstate = dependencies(
        "androidx.lifecycle:lifecycle-viewmodel-savedstate:2.2.0".dep,
        annotation,
        lifecycle_livedate_core,
        lifecycle_viewmodel,
        savedstate
    )

    val core = dependencies(
        "androidx.core:core:1.3.1".dep,
        annotation,
        lifecycle_runtime,
        versionedparcelable
    )

    val core_ktx = dependencies(
        "androidx.core:core-ktx:1.3.1".dep,
        annotation,
        core
    )

    val activity = dependencies(
        "androidx.activity:activity:1.1.0".dep,
        annotation,
        core,
        lifecycle_runtime,
        lifecycle_viewmodel,
        lifecycle_viewmodel_savedstate,
        savedstate
    )

    val loader = dependencies(
        "androidx.loader:loader:1.1.0".dep,
        annotation,
        collection,
        core,
        lifecycle_livedate_core,
        lifecycle_viewmodel
    )

    val customview = dependencies(
        "androidx.customview:customview:1.1.0".dep,
        annotation,
        core
    )

    val viewpager = dependencies(
        "androidx.viewpager:viewpager:1.0.0".dep,
        annotation,
        core,
        customview
    )

    val fragment = dependencies(
        "androidx.fragment:fragment:1.2.5".dep,
        annotation,
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
        "androidx.activity:activity-ktx:1.1.0".dep,
        activity,
        core_ktx,
        lifecycle_runtime_ktx,
        lifecycle_viewmodel_ktx
    )

    val drawerlayout = dependencies(
        "androidx.drawerlayout:drawerlayout:1.1.0".dep,
        annotation,
        core,
        customview
    )

    val documentfile = dependencies(
        "androidx.documentfile:documentfile:1.0.1".dep,
        annotation
    )
    val localbroadcastmanager = dependencies(
        "androidx.localbroadcastmanager:localbroadcastmanager:1.0.0".dep,
        annotation
    )
    val print = dependencies(
        "androidx.print:print:1.0.0".dep,
        annotation
    )
    val legacy_utils = dependencies(
        "androidx.legacy:legacy-support-core-utils:1.0.0".dep,
        documentfile,
        localbroadcastmanager,
        print,
        annotation,
        core,
        loader
    )

    val vectordrawable = dependencies(
        "androidx.vectordrawable:vectordrawable:1.1.0".dep,
        androidx.annotation,
        androidx.collection,
        androidx.core
    )

    val asynclayoutinflater = dependencies(
        "androidx.asynclayoutinflater:asynclayoutinflater:1.0.0".dep,
        androidx.annotation,
        core
    )

    val coordinatorlayout = dependencies(
        "androidx.coordinatorlayout:coordinatorlayout:1.1.0".dep,
        androidx.annotation,
        androidx.collection,
        androidx.core,
        androidx.customview
    )

    val interpolator = dependencies(
        "androidx.interpolator:interpolator:1.0.0".dep,
        androidx.annotation
    )
    val cursoradapter = dependencies(
        "androidx.cursoradapter:cursoradapter:1.0.0".dep,
        annotation
    )

    val slidingpanelayout = dependencies(
        "androidx.slidingpanelayout:slidingpanelayout:1.0.0".dep,
        androidx.annotation,
        androidx.customview
    )

    val swiperefreshlayout = dependencies(
        "androidx.swiperefreshlayout:swiperefreshlayout:1.0.0".dep,
        androidx.annotation,
        androidx.core,
        androidx.interpolator
    )

    val legacy_ui = dependencies(
        "androidx.legacy:legacy-support-core-ui:1.0.0".dep,
        androidx.asynclayoutinflater,
        coordinatorlayout,
        androidx.cursoradapter,
        androidx.interpolator,
        androidx.slidingpanelayout,
        androidx.swiperefreshlayout,
        core,
        customview,
        drawerlayout,
        legacy_utils,
        viewpager
    )
    val recyclerview = dependencies(
        "androidx.recyclerview:recyclerview:1.1.0".dep,
        annotation,
        collection,
        core,
        customview
    )
    val transition = dependencies(
        "androidx.transition:transition:1.3.1".dep,
        annotation,
        collection,
        core,
        lifecycle_runtime
    )
    val navigation_common = dependencies(
        "androidx.navigation:navigation-common:2.3.0".dep,
        annotation,
        collection,
        core
    )

    val appcompat = dependencies(
        "androidx.appcompat:appcompat:1.2.0".dep,
        "androidx.appcompat:appcompat-resources:1.2.0".dep,
        annotation,
        core,
        cursoradapter,
        drawerlayout,
        fragment,
        collection
    )
    val constraintlayout = dependencies(
        "androidx.constraintlayout:constraintlayout:2.0.1".dep,
        "androidx.constraintlayout:constraintlayout-solver:2.0.1".dep,
        appcompat,
        core
    )

    val navigation_ui_ktx = dependencies(
        "androidx.navigation:navigation-ui-ktx:2.3.0",
        "androidx.navigation:navigation-ui:2.3.0"
    )

    val navigation_common_ktx = dependencies(
        "androidx.navigation:navigation-common:2.3.0".dep,
        annotation,
        collection,
        core
    )

    val navigation_runtime = dependencies(
        "androidx.navigation:navigation-runtime:2.3.0".dep,
        activity,
        lifecycle_viewmodel,
        lifecycle_viewmodel_savedstate,
        navigation_common,
        savedstate
    )

    val navigation_runtime_ktx = dependencies(
        navigation_runtime,
        navigation_common_ktx,
        activity_ktx,
        lifecycle_viewmodel_ktx
    )

    val navigation_fragment = dependencies(
        "androidx.navigation:navigation-fragment:2.3.0".dep,
        fragment,
        navigation_runtime
    )
    val navigation_fragment_ktx = dependencies(
        "androidx.navigation:navigation-fragment-ktx:2.3.0".dep,
        navigation_fragment,
        navigation_runtime,
        fragment
    )
}

object google {
    val material = dependencies(
        "com.google.android.material:material:1.2.0".dep,
        androidx.appcompat,
        androidx.cardview,
        androidx.core,
        androidx.annotation,
        androidx.legacy_ui,
        androidx.legacy_utils,
        androidx.recyclerview,
        androidx.transition
    )
}