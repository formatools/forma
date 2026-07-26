package tools.forma.examples.android.firebase.root

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics

/**
 * Real **usage** of Google Firebase first-party SDKs (not classpath-only).
 * Dummy `google-services.json` on `:binary` is enough for local/CI assembleDebug.
 */
class FirebaseSampleApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        FirebaseCrashlytics.getInstance().log("Forma Android 14 firebase example started")
        FirebaseCrashlytics.getInstance().setCustomKey("forma_example", "14-google-firebase")
    }
}
