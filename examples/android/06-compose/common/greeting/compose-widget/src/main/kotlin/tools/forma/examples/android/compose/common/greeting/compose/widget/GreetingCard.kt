package tools.forma.examples.android.compose.common.greeting.compose.widget

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun GreetingCard(text: String) {
    Text(text = text)
}

@Preview
@Composable
private fun PreviewGreeting() {
    GreetingCard("preview")
}
