package tools.forma.examples.android.navports.feature.list.impl

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import tools.forma.examples.android.navports.feature.list.viewbinding.databinding.FragmentListBinding
import tools.forma.examples.android.navports.navigation.api.AppDestination
import tools.forma.examples.android.navports.navigation.api.NavigatorProvider

/**
 * Feature UI talks only to [tools.forma.examples.android.navports.navigation.api.Navigator].
 * No findNavController, no Safe Args *Directions, no graph R.id.
 */
class ListFragment : Fragment() {

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!

    private val navigator
        get() = (requireActivity() as NavigatorProvider).navigator

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.openDetail.setOnClickListener {
            navigator.navigate(AppDestination.ItemDetail(itemId = DEMO_ITEM_ID))
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        const val DEMO_ITEM_ID = 42L
    }
}
