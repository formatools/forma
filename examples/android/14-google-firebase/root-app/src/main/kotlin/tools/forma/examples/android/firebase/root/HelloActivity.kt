package tools.forma.examples.android.firebase.root

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics

class HelloActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseAnalytics.getInstance(this).logEvent(
            "forma_firebase_example_open",
            Bundle().apply { putString("step", "14") },
        )
        FirebaseCrashlytics.getInstance().log("HelloActivity opened")

        val tv = TextView(this)
        tv.text = "Forma Android 14 — Firebase Crashlytics + Analytics"
        setContentView(tv)
    }
}
