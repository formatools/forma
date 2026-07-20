package tools.forma.examples.android.targetplugins.feature.hello.impl

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import tools.forma.examples.android.targetplugins.feature.hello.viewbinding.databinding.HelloFeatureBinding
import tools.forma.examples.android.targetplugins.feature.hello.res.R as HelloR

class HelloScreen(context: Context) {
    val view: View = run {
        val binding = HelloFeatureBinding.inflate(LayoutInflater.from(context))
        binding.message.setText(HelloR.string.hello_title)
        binding.root
    }
}
