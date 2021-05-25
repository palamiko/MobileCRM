package android.bignerdranch.mobilecrm.ui.fragments.clients

import android.bignerdranch.mobilecrm.ui.fragments.task.ClientCardView
import android.bignerdranch.mobilecrm.ui.theme.MyTestComposeTheme
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import kotlinx.serialization.ExperimentalSerializationApi

open class ClientCardCompose : Fragment() {
    open val args: ClientCardComposeArgs by navArgs()

    @ExperimentalSerializationApi
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                ComposeFragment {
                    ClientCardView (
                        args = args,
                        lifecycleOwner = viewLifecycleOwner,
                        navController = findNavController(),
                        graphName = "clientGraph"
                    )
                }
            }
        }
    }

    @ExperimentalSerializationApi
    @Composable
    fun ComposeFragment(
        container: @Composable () -> Unit
    ) {
        MyTestComposeTheme {
            container()
        }
    }
}







