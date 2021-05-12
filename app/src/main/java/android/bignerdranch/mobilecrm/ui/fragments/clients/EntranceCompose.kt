package android.bignerdranch.mobilecrm.ui.fragments.clients

import android.bignerdranch.mobilecrm.model.modelsDB.Entrance
import android.bignerdranch.mobilecrm.model.viewModels.ClientsViewModel
import android.bignerdranch.mobilecrm.ui.composeFun.EntranceListItem
import android.bignerdranch.mobilecrm.ui.theme.MyTestComposeTheme
import android.bignerdranch.mobilecrm.ui.theme.SecondaryLightColor
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
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

class EntranceCompose : Fragment() {
    private val args: EntranceComposeArgs by navArgs()

    @ExperimentalSerializationApi
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return ComposeView(requireContext()).apply {
            setContent {
                ComposeFragment {
                    EntranceList()
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
    fun EntranceList(clientsViewModel: ClientsViewModel = viewModel()) {
        val entranceList by clientsViewModel.entranceList.observeAsState()
        clientsViewModel.getEntrances(args.buildingId)

        Column {
            TitlePage(modifier = Modifier.padding(top = 4.dp))

            LazyColumn {
                items(entranceList ?: listOf()) { item ->
                    ItemEntrance(entrance = item)
                }
            }
        }

    }

    @Composable
    fun ItemEntrance(entrance: Entrance) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .requiredHeight(44.dp)
                .clickable { navigateToClientsInEntrance(entrance) }
        ) {
            EntranceListItem(entrance = entrance)
            //SmallListItem(text = entrance.number)
        }
    }

    @Composable
    fun TitlePage(modifier: Modifier) {
        /** Заголовок с полями списка */

        Surface(elevation = 4.dp) {

            Row(modifier = Modifier
                .fillMaxWidth()
                .height(36.dp)) {
                Spacer(modifier = Modifier.width(16.dp))
                Column() {
                    Text(text = "Номер", modifier = modifier,
                        color = if (isSystemInDarkTheme()) SecondaryLightColor else MaterialTheme.colors.secondaryVariant)
                }

                Spacer(modifier = Modifier.width(16.dp))
                Column() {
                    Text(text = "Узел в", modifier = modifier,
                        color = if (isSystemInDarkTheme()) SecondaryLightColor else MaterialTheme.colors.secondaryVariant)
                }

                Spacer(modifier = Modifier.width(16.dp))
                Column() {
                    Text(text = "Коментарий", modifier = modifier,
                        color = if (isSystemInDarkTheme()) SecondaryLightColor else MaterialTheme.colors.secondaryVariant)
                }
            }
        }
    }

    private fun navigateToClientsInEntrance(entrance: Entrance) {
        val navController = findNavController()
        navController.navigate(
            EntranceComposeDirections.actionEntranceComposeToClientsInEntranceCompose(
                entranceId = entrance.id, buildNumber = args.buildNumber, streetName = args.streetName
            )
        )
    }
}