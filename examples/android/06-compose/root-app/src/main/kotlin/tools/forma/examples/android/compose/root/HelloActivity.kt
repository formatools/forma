package tools.forma.examples.android.compose.root

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import tools.forma.examples.android.compose.common.greeting.compose.widget.GreetingCard

class HelloActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GreetingCard(text = "Forma Android 06 compose")
        }
    }
}
