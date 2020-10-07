@file:Suppress("ClassName", "MemberVisibilityCanBePrivate")

object versions {
    const val funktionale = "1.2"
    const val timber = "4.7.1"
    const val coil = "0.8.0"

    object jetbrains {
        const val annotations = "20.0.0"
        const val coroutines = "1.3.9"
    }

    object androidx {
        const val activity = "1.1.0"
        const val annotation = "1.1.0"
        const val arch = "2.1.0"
        const val asynclayoutinflater = "1.0.0"
        const val appcompat = "1.2.0"
        const val cardview = "1.0.0"
        const val collection = "1.0.0"
        const val core = "1.3.1"
        const val core_common = "2.1.0"
        const val coordinatorlayout = "1.1.0"
        const val constraintlayout = "2.0.1"
        const val customview = "1.1.0"
        const val cursoradapter = "1.0.0"
        const val documentfile = "1.0.1"
        const val drawerlayout = "1.1.0"
        const val interpolator = "1.0.0"
        const val fragment = "1.2.5"
        const val legacy = "1.0.0"
        const val lifecycle = "2.2.0"
        const val loader = "1.1.0"
        const val localbroadcastmanager = "1.0.0"
        const val navigation = "2.3.0"
        const val savedstate = "1.0.0"
        const val slidingpanelayout = "1.0.0"
        const val swiperefreshlayout = "1.0.0"
        const val paging = "2.1.2"
        const val recyclerview = "1.1.0"
        const val transition = "1.3.1"
        const val vectordrawable = "1.1.0"
        const val versionedparcelable = "1.1.0"
        const val viewpager = "1.0.0"
    }

    object google {
        const val material = "1.2.0"
    }

    object test {
        const val espresso = "3.2.0"
        const val junit = "4.12"
        const val junit_ext = "1.1.1"
        const val hamcrest = "1.3"
    }
}

object jetbrains {
    val annotations = "org.jetbrains:annotations:${versions.jetbrains.annotations}".dep
}

val kotlin = com.stepango.forma.dependencies.kotlin
val dataBinding = com.stepango.forma.dependencies.dataBinding

object kotlinx {
    val coroutines_core = deps(
        "org.jetbrains.kotlinx:kotlinx-coroutines-core:${versions.jetbrains.coroutines}".dep,
        kotlin.stdlib
    )
    val coroutines_android = deps(
        "org.jetbrains.kotlinx:kotlinx-coroutines-android:${versions.jetbrains.coroutines}".dep,
        kotlinx.coroutines_core
    )
}

object androidx {
    val annotation = "androidx.annotation:annotation:${versions.androidx.annotation}".dep

    val collection = deps(
        androidx.annotation,
        "androidx.collection:collection:${versions.androidx.collection}".dep
    )
    val versionedparcelable = deps(
        "androidx.versionedparcelable:versionedparcelable:${versions.androidx.versionedparcelable}".dep,
        androidx.annotation,
        androidx.collection
    )
    val cardview = deps(
        "androidx.cardview:cardview:${versions.androidx.cardview}".dep,
        androidx.annotation
    )
    val core_common = deps(
        androidx.annotation,
        "androidx.arch.core:core-common:${versions.androidx.core_common}".dep
    )
    val lifecycle_common = deps(
        androidx.annotation,
        "androidx.lifecycle:lifecycle-common:${versions.androidx.lifecycle}".dep
    )
    val lifecycle_extensions = deps(
        androidx.annotation,
        "androidx.lifecycle:lifecycle-extensions:${versions.androidx.lifecycle}".dep
    )
    val lifecycle_runtime = deps(
        "androidx.lifecycle:lifecycle-runtime:${versions.androidx.lifecycle}".dep,
        androidx.core_common,
        androidx.lifecycle_common
    )
    val lifecycle_viewmodel = deps(
        androidx.annotation,
        "androidx.lifecycle:lifecycle-viewmodel:${versions.androidx.lifecycle}".dep
    )

    val lifecycle_viewmodel_ktx = deps(
        "androidx.lifecycle:lifecycle-viewmodel-ktx:${versions.androidx.lifecycle}".dep,
        androidx.lifecycle_viewmodel,
        kotlinx.coroutines_android
    )

    val lifecycle_runtime_ktx = deps(
        "androidx.lifecycle:lifecycle-runtime-ktx:${versions.androidx.lifecycle}".dep,
        androidx.annotation,
        kotlinx.coroutines_android
    )

    val savedstate = deps(
        "androidx.savedstate:savedstate:${versions.androidx.savedstate}".dep,
        androidx.annotation,
        androidx.core_common,
        androidx.lifecycle_common
    )

    val core_runtime = deps(
        "androidx.arch.core:core-runtime:${versions.androidx.arch}".dep,
        androidx.annotation,
        androidx.core_common
    )

    val lifecycle_livedate_core = deps(
        "androidx.lifecycle:lifecycle-livedata-core:${versions.androidx.lifecycle}".dep,
        androidx.core_common,
        androidx.core_runtime
    )

    val lifecycle_viewmodel_savedstate = deps(
        "androidx.lifecycle:lifecycle-viewmodel-savedstate:${versions.androidx.lifecycle}".dep,
        androidx.annotation,
        androidx.lifecycle_livedate_core,
        androidx.lifecycle_viewmodel,
        androidx.savedstate
    )

    val core = deps(
        "androidx.core:core:${versions.androidx.core}".dep,
        androidx.annotation,
        androidx.lifecycle_runtime,
        androidx.versionedparcelable
    )

    val core_ktx = deps(
        "androidx.core:core-ktx:${versions.androidx.core}".dep,
        androidx.annotation,
        androidx.core
    )

    val activity = deps(
        "androidx.activity:activity:${versions.androidx.activity}".dep,
        androidx.annotation,
        androidx.core,
        androidx.lifecycle_runtime,
        androidx.lifecycle_viewmodel,
        androidx.lifecycle_viewmodel_savedstate,
        androidx.savedstate
    )

    val loader = deps(
        "androidx.loader:loader:${versions.androidx.loader}".dep,
        androidx.annotation,
        androidx.collection,
        androidx.core,
        androidx.lifecycle_livedate_core,
        androidx.lifecycle_viewmodel
    )

    val customview = deps(
        "androidx.customview:customview:${versions.androidx.customview}".dep,
        androidx.annotation,
        androidx.core
    )

    val viewpager = deps(
        "androidx.viewpager:viewpager:${versions.androidx.viewpager}".dep,
        androidx.annotation,
        androidx.core,
        androidx.customview
    )

    val fragment = deps(
        "androidx.fragment:fragment:${versions.androidx.fragment}".dep,
        androidx.annotation,
        androidx.activity,
        androidx.collection,
        androidx.core,
        androidx.lifecycle_livedate_core,
        androidx.lifecycle_viewmodel,
        androidx.lifecycle_viewmodel_savedstate,
        androidx.loader,
        androidx.viewpager
    )

    val activity_ktx = deps(
        "androidx.activity:activity-ktx:${versions.androidx.activity}".dep,
        androidx.activity,
        androidx.core_ktx,
        androidx.lifecycle_runtime_ktx,
        androidx.lifecycle_viewmodel_ktx
    )

    val drawerlayout = deps(
        "androidx.drawerlayout:drawerlayout:${versions.androidx.drawerlayout}".dep,
        androidx.annotation,
        androidx.core,
        androidx.customview
    )

    val documentfile = deps(
        "androidx.documentfile:documentfile:${versions.androidx.documentfile}".dep,
        androidx.annotation
    )
    val localbroadcastmanager = deps(
        "androidx.localbroadcastmanager:localbroadcastmanager:${versions.androidx.localbroadcastmanager}".dep,
        androidx.annotation
    )
    val print = deps(
        "androidx.print:print:1.0.0".dep,
        androidx.annotation
    )
    val legacy_utils = deps(
        "androidx.legacy:legacy-support-core-utils:${versions.androidx.legacy}".dep,
        androidx.documentfile,
        androidx.localbroadcastmanager,
        androidx.print,
        androidx.annotation,
        androidx.core,
        androidx.loader
    )

    val vectordrawable = deps(
        "androidx.vectordrawable:vectordrawable:${versions.androidx.vectordrawable}".dep,
        androidx.annotation,
        androidx.collection,
        androidx.core
    )

    val asynclayoutinflater = deps(
        "androidx.asynclayoutinflater:asynclayoutinflater:${versions.androidx.asynclayoutinflater}".dep,
        androidx.annotation,
        androidx.core
    )

    val coordinatorlayout = deps(
        "androidx.coordinatorlayout:coordinatorlayout:${versions.androidx.coordinatorlayout}".dep,
        androidx.annotation,
        androidx.collection,
        androidx.core,
        androidx.customview
    )

    val interpolator = deps(
        "androidx.interpolator:interpolator:${versions.androidx.interpolator}".dep,
        androidx.annotation
    )
    val cursoradapter = deps(
        "androidx.cursoradapter:cursoradapter:${versions.androidx.cursoradapter}".dep,
        androidx.annotation
    )

    val slidingpanelayout = deps(
        "androidx.slidingpanelayout:slidingpanelayout:${versions.androidx.slidingpanelayout}".dep,
        androidx.annotation,
        androidx.customview
    )

    val swiperefreshlayout = deps(
        "androidx.swiperefreshlayout:swiperefreshlayout:${versions.androidx.swiperefreshlayout}".dep,
        androidx.annotation,
        androidx.core,
        androidx.interpolator
    )

    val paging = transitiveDeps(
        "androidx.paging:paging-runtime:${versions.androidx.paging}",
        "androidx.paging:paging-common:${versions.androidx.paging}"
    )

    val legacy_ui = deps(
        "androidx.legacy:legacy-support-core-ui:${versions.androidx.legacy}".dep,
        androidx.asynclayoutinflater,
        androidx.coordinatorlayout,
        androidx.cursoradapter,
        androidx.interpolator,
        androidx.slidingpanelayout,
        androidx.swiperefreshlayout,
        androidx.core,
        androidx.customview,
        androidx.drawerlayout,
        androidx.legacy_utils,
        androidx.viewpager
    )
    val recyclerview = deps(
        "androidx.recyclerview:recyclerview:${versions.androidx.recyclerview}".dep,
        androidx.annotation,
        androidx.collection,
        androidx.core,
        androidx.customview
    )
    val transition = deps(
        "androidx.transition:transition:${versions.androidx.transition}".dep,
        androidx.annotation,
        androidx.collection,
        androidx.core,
        androidx.lifecycle_runtime
    )
    val navigation_common = deps(
        "androidx.navigation:navigation-common:${versions.androidx.navigation}".dep,
        androidx.annotation,
        androidx.collection,
        androidx.core
    )

    val appcompat = deps(
        "androidx.appcompat:appcompat:${versions.androidx.appcompat}".dep,
        "androidx.appcompat:appcompat-resources:${versions.androidx.appcompat}".dep,
        androidx.annotation,
        androidx.core,
        androidx.cursoradapter,
        androidx.drawerlayout,
        androidx.fragment,
        androidx.collection
    )
    val constraintlayout = deps(
        "androidx.constraintlayout:constraintlayout:${versions.androidx.constraintlayout}".dep,
        "androidx.constraintlayout:constraintlayout-solver:${versions.androidx.constraintlayout}".dep,
        androidx.appcompat,
        androidx.core
    )

    val navigation_ui_ktx = deps(
        "androidx.navigation:navigation-ui-ktx:${versions.androidx.navigation}",
        "androidx.navigation:navigation-ui:${versions.androidx.navigation}"
    )

    val navigation_common_ktx = deps(
        "androidx.navigation:navigation-common:${versions.androidx.navigation}".dep,
        androidx.annotation,
        androidx.collection,
        androidx.core
    )

    val navigation_runtime = deps(
        "androidx.navigation:navigation-runtime:${versions.androidx.navigation}".dep,
        androidx.activity,
        androidx.lifecycle_viewmodel,
        androidx.lifecycle_viewmodel_savedstate,
        androidx.navigation_common,
        androidx.savedstate
    )

    val navigation_runtime_ktx = deps(
        androidx.navigation_runtime,
        androidx.navigation_common_ktx,
        androidx.activity_ktx,
        androidx.lifecycle_viewmodel_ktx
    )

    val navigation_fragment = deps(
        "androidx.navigation:navigation-fragment:${versions.androidx.navigation}".dep,
        androidx.fragment,
        androidx.navigation_runtime
    )
    val navigation_fragment_ktx = deps(
        "androidx.navigation:navigation-fragment-ktx:${versions.androidx.navigation}".dep,
        androidx.navigation_fragment,
        androidx.navigation_runtime,
        androidx.fragment
    )
}

object google {
    val material = deps(
        "com.google.android.material:material:${versions.google.material}".dep,
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

object javax {
    val inject = deps(
        "javax.inject:javax.inject:1".dep
    )
}

object test {
    val junit = deps(
        "junit:junit:${versions.test.junit}".dep,
        "org.hamcrest:hamcrest-core:${versions.test.hamcrest}".dep
    )

    val junit_ext = deps(
        "androidx.test.ext:junit:${versions.test.junit_ext}".dep
    )

    val espresso = deps(
        "androidx.test.espresso:espresso-core:${versions.test.espresso}".dep
    )
}

object io {
    val coil = transitiveDeps(
        "io.coil-kt:coil:${versions.coil}",
        "io.coil-kt:coil-base:${versions.coil}"
    )
}

object jakewharton {
    val timber = "com.jakewharton.timber:timber:${versions.timber}".dep
}
