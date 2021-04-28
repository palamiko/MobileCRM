package android.bignerdranch.mobilecrm.ui.fragments.clients

import android.bignerdranch.mobilecrm.model.modelsDB.ClientsEntrance
import android.bignerdranch.mobilecrm.model.viewModels.ClientsViewModel
import android.bignerdranch.mobilecrm.ui.composeFun.ClientListItem
import android.bignerdranch.mobilecrm.ui.theme.MyTestComposeTheme
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.navigation.fragment.navArgs
import kotlinx.serialization.ExperimentalSerializationApi

class ClientsInEntranceCompose : Fragment() {
    private val args: ClientsInEntranceComposeArgs by navArgs()

    @ExperimentalSerializationApi
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return ComposeView(requireContext()).apply {
            setContent {
                ComposeFragment {
                    ClientsList()
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
    fun ClientsList(clientsViewModel: ClientsViewModel = viewModel()) {
        val clientsList by clientsViewModel.clientsEntrance.observeAsState()
        clientsViewModel.getClientsEntrance(args.entranceId)

        Column() {
            TitlePage(Modifier.padding(start = 16.dp))

            LazyColumn {
                items(clientsList ?: listOf()) { item ->
                    ItemClients(clients = item)
                }
            }
        }
    }

    @Composable
    fun ItemClients(clients: ClientsEntrance) {
        Box(
            modifier = Modifier
                .requiredHeight(44.dp)
                .clickable { }
        ) {
            ClientListItem(clients)
        }
    }

    @Composable
    fun TitlePage(modifier: Modifier) {

        Surface(elevation = 4.dp) {
            Row(modifier = Modifier.fillMaxWidth().height(32.dp)) {
                Column(Modifier.requiredWidth(43.dp)) {
                    Text(text = "Кв", modifier = modifier)
                }
                Column(Modifier.requiredWidth(107.dp)) {
                    Text(text = "Услуга", modifier = modifier)
                }
                Column(Modifier.requiredWidth(140.dp)) {
                    Text(text = "Статус", modifier = modifier)
                }
                Column(Modifier.requiredWidth(80.dp)) {
                    Text(text = "Этаж")
                }
            }
        }
    }
}