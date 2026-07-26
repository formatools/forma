package tools.forma.examples.android.navports.feature.detail.impl

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import tools.forma.examples.android.navports.feature.detail.viewbinding.databinding.FragmentDetailBinding
import tools.forma.examples.android.navports.navigation.api.NavigatorProvider

/**
 * Reads the destination id from a plain Bundle key that matches the nav-graph argument
 * name. Safe Args still *writes* that key from the root adapter — feature code never
 * imports *Args or androidx.navigation.
 */
class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    private val navigator
        get() = (requireActivity() as NavigatorProvider).navigator

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val itemId = requireArguments().getLong(ARG_ITEM_ID)
        binding.title.text = "Detail itemId=$itemId (no Nav imports)"
        binding.goBack.setOnClickListener { navigator.back() }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        /** Must match `android:name` on the nav-graph `<argument>`. */
        const val ARG_ITEM_ID = "itemId"
    }
}
