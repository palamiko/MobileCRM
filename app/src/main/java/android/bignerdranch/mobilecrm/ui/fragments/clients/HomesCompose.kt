package android.bignerdranch.mobilecrm.ui.fragments.clients

import android.bignerdranch.mobilecrm.model.modelsDB.Home
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

class HomesCompose : Fragment() {
    private val args: HomesComposeArgs by navArgs()

    @ExperimentalSerializationApi
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return ComposeView(requireContext()).apply {
            setContent {
                ComposeFragment {
                    HomesList()
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
    fun HomesList(clientsViewModel: ClientsViewModel = viewModel()) {
        // Слушатель LiveData
        val streetList by clientsViewModel.homeList.observeAsState()
        // Запрос списка подъездов
        clientsViewModel.getHomes(args.districtId, args.streetId)
        // Создание списка подъездов
        LazyColumn {
            items(streetList ?: listOf()) { item ->
                ItemHome(item)
            }
        }
    }

    @Composable
    fun ItemHome(home: Home) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .requiredHeight(44.dp)
                .clickable { navigateToEntrance(home.id, home.number, args.streetName) }
        ) {
            SmallListItem(text = home.number)
        }
    }

    private fun navigateToEntrance(buildingId: Int, buildingNumber: String, streetName: String) {
        val navController = findNavController()
        navController.navigate(
            HomesComposeDirections.actionHomesComposeToEntranceCompose(
                buildingId = buildingId, buildNumber = buildingNumber, streetName = streetName
            )
        )
    }
}