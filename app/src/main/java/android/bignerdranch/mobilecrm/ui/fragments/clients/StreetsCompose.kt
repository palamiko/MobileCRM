package android.bignerdranch.mobilecrm.ui.fragments.clients

import android.bignerdranch.mobilecrm.model.modelsDB.Street
import android.bignerdranch.mobilecrm.model.viewModels.ClientsViewModel
import android.bignerdranch.mobilecrm.ui.composeFun.SmallListItem
import android.bignerdranch.mobilecrm.ui.theme.MyTestComposeTheme
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import kotlinx.serialization.ExperimentalSerializationApi

class StreetsCompose : Fragment() {
    private val args: StreetsComposeArgs by navArgs()

    @ExperimentalSerializationApi
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return ComposeView(requireContext()).apply {
            setContent {
                ComposeFragment {
                    StreetsList()
                }
            }
        }
    }

    @Composable
    fun ComposeFragment(container: @Composable () -> Unit) {
        MyTestComposeTheme {
            container()
        }
    }

    @ExperimentalSerializationApi
    @Composable
    fun StreetsList(clientsViewModel: ClientsViewModel = viewModel()) {
        // Слушатель LiveData
        val streetList by clientsViewModel.streetList.observeAsState()
        // Запрос списка улиц
        clientsViewModel.getStreet(args.districtId)
        // Создание списка улиц
        LazyColumn {
            items(streetList ?: listOf()) { item ->
                ItemStreet(item)
            }
        }
    }

    @Composable
    fun ItemStreet(street: Street) {
        /** Один элемент списка */

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .requiredHeight(44.dp)
                .clickable { navigateToHomes(args.districtId, street.id)}
        ) { SmallListItem(text = street.name) }
    }

    private fun navigateToHomes(district_id: Int, street_id: Int) {
        val navController = findNavController()
        navController.navigate(
            StreetsComposeDirections.actionStreetsComposeToHomesCompose(
                districtId = district_id, streetId = street_id
            )
        )
    }
}