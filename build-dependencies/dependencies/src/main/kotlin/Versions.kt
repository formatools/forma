object versions {
    const val coil = "2.7.0"

    object jetbrains {
        // Last line friendly with Kotlin 2.3.21 + AndroidX caps below
        const val coroutines = "1.10.2"
    }

    object androidx {
        // F-087 ceiling: AGP 9.3 + compileSdk/targetSdk 37 (AAR minCompileSdk probed)
        // core 1.19 → minCompileSdk 37 + minAGP 9.1; activity 1.13 → minCompileSdk 36
        // paging stays 2.x (sample PagedList APIs); OkHttp/Retrofit 4/2 separate
        const val activity = "1.13.0"
        const val annotation = "1.10.0"
        const val arch = "2.2.0"
        const val asynclayoutinflater = "1.0.0"
        const val appcompat = "1.7.1" // latest stable (1.8 still alpha)
        const val cardview = "1.0.0"
        const val collection = "1.6.0"
        const val core = "1.19.0"
        const val core_common = "2.2.0"
        const val coordinatorlayout = "1.3.0"
        const val constraintlayout = "2.2.1"
        const val customview = "1.2.0"
        const val cursoradapter = "1.0.0"
        const val documentfile = "1.0.1"
        const val drawerlayout = "1.2.0"
        const val interpolator = "1.0.0"
        const val fragment = "1.8.9" // latest stable (1.9 still alpha)
        const val legacy = "1.0.0"
        const val lifecycle = "2.11.0"
        const val loader = "1.1.0"
        const val localbroadcastmanager = "1.0.0"
        // Sample uses paging 2.x APIs (PagedList / PageKeyedDataSource) — do not jump to 3.x
        const val navigation = "2.9.8" // latest stable (2.10 still alpha)
        // activity 1.13+ DialogFragment surface needs navigationevent on compile classpath
        const val navigationevent = "1.1.2"
        const val savedstate = "1.5.0"
        const val slidingpanelayout = "1.2.0"
        const val swiperefreshlayout = "1.2.0"
        const val sqlite = "2.7.0"
        const val paging = "2.1.2"
        const val recyclerview = "1.4.0"
        const val room = "2.8.4"
        const val transition = "1.7.0"
        const val vectordrawable = "1.2.0"
        const val versionedparcelable = "1.2.1"
        const val viewpager = "1.1.0"
        // Compose BOM line 1.11.x stable (1.12 still beta)
        const val compose = "1.11.4"
    }

    object google {
        const val material = "1.14.0"
        const val dagger = "2.60.1"
        const val play_core = "1.10.3"
        const val gson = "2.13.2"
    }

    object test {
        const val espresso = "3.6.1"
        const val junit = "4.13.2"
        const val junit_ext = "1.2.1"
        const val hamcrest = "1.3"
        const val mockk = "1.13.14"
    }

    object squareup {
        // Keep OkHttp 4.x + Retrofit 2.x (sample NetworkModule); 5/3 = separate migration
        const val retrofit = "2.11.0"
        const val okhttp = "4.12.0"
    }

    object viewbinding {
        const val viewbindingpropertydelegate = "1.5.9"
    }
}
