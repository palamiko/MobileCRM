package android.bignerdranch.mobilecrm.ui.fragments.clients

import android.bignerdranch.mobilecrm.model.modelsDB.ClientsEntrance
import android.bignerdranch.mobilecrm.model.otherModel.Services
import android.bignerdranch.mobilecrm.model.viewModels.ClientsViewModel
import android.bignerdranch.mobilecrm.ui.composeFun.ClientListItem
import android.bignerdranch.mobilecrm.ui.theme.MyTestComposeTheme
import android.bignerdranch.mobilecrm.ui.theme.SecondaryLightColor
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.fragment.findNavController
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
                ComposeFragment() {
                    ClientsList()
                }
            }
        }
    }

    @ExperimentalSerializationApi
    @Composable
    fun ComposeFragment(
        clientsViewModel: ClientsViewModel = viewModel(),
        container: @Composable () -> Unit
    ) {
        val entranceId by rememberSaveable { mutableStateOf(args.entranceId) }
        clientsViewModel.getClientsEntrance(
            entrance_id = entranceId,
            service_name = Services().all_services
        )

        MyTestComposeTheme {
            container()
        }
    }


    @ExperimentalSerializationApi
    @Composable
    fun ClientsList(clientsViewModel: ClientsViewModel = viewModel()) {
        val clientsList by clientsViewModel.clientsEntrance.observeAsState()


        Column {
            TitlePage(Modifier.padding(top = 4.dp), entrance_id = args.entranceId)
            RecyclerList(
                listItem = clientsList
            )
        }
    }

    @Composable
    fun ItemClients(clients: ClientsEntrance) {
        Box(
            modifier = Modifier
                .requiredHeight(44.dp)
                .clickable {
                    navigateToClientsCard(clients = clients)
                }
        ) {
            ClientListItem(clients)
        }
    }

    @ExperimentalSerializationApi
    @Composable
    fun TitlePage(modifier: Modifier, entrance_id: Int) {
        /** Заголовок с полями списка */

        Surface(elevation = 4.dp) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                //Spacer(modifier = Modifier.width(16.dp))
                Column(Modifier.requiredWidth(27.dp)) {
                    Text(
                        text = "Кв", modifier = modifier,
                        color = if (isSystemInDarkTheme()) SecondaryLightColor else MaterialTheme.colors.secondaryVariant
                    )
                }

                Column(Modifier.requiredWidth(100.dp)) {
                    var expanded by remember { mutableStateOf(false) }

                    Button(
                        onClick = { if (expanded) !expanded else expanded = true },
                        modifier = Modifier.height(34.dp),
                        colors = ButtonDefaults.textButtonColors(
                            backgroundColor = if (isSystemInDarkTheme()) SecondaryLightColor else MaterialTheme.colors.secondaryVariant
                        )
                    ) {
                        Text(
                            "Услуга",
                            color = if (isSystemInDarkTheme()) Color.White else Color.Black
                        )
                        DropdownDemo(modifier, entrance_id = entrance_id, expended = expanded,
                            onExpandedChange = { expanded = it })
                    }
                }

                Column(Modifier.requiredWidth(98.dp)) {
                    Text(
                        text = "Статус", modifier = modifier,
                        color = if (isSystemInDarkTheme()) SecondaryLightColor else MaterialTheme.colors.secondaryVariant
                    )
                }
                Column(Modifier.requiredWidth(40.dp)) {
                    Text(
                        text = "Этаж", modifier = modifier,
                        color = if (isSystemInDarkTheme()) SecondaryLightColor else MaterialTheme.colors.secondaryVariant
                    )
                }

            }
        }
    }

    @Composable
    fun RecyclerList(listItem: List<ClientsEntrance>?) {
        /** Список с абонентами */

        LazyColumn {
            items(listItem ?: listOf()) { item ->
                ItemClients(clients = item)
            }
        }
    }

    @ExperimentalSerializationApi
    @Composable
    fun DropdownDemo(
        modifier: Modifier,
        clientsViewModel: ClientsViewModel = viewModel(),
        entrance_id: Int,
        expended: Boolean,
        onExpandedChange: (Boolean) -> Unit
    ) {
        //var expanded by remember { mutableStateOf(false) }
        var expanded = expended
        val items = mapOf(
            "Все" to "all", "Интернет" to "internet", "Телевидение" to "tv", "Домофон" to "domofon"
        )
        val (selectedOption, onOptionSelected) = remember { mutableStateOf(items["Все"]) }

        Box(modifier = modifier) {

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .background(if (isSystemInDarkTheme()) SecondaryLightColor else MaterialTheme.colors.secondaryVariant)
            ) {
                items.forEach { serviceName ->
                    Column(
                        modifier = Modifier.height(38.dp),
                        verticalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .selectable(
                                    selected = (serviceName.key == selectedOption)
                                ) {
                                    onOptionSelected(serviceName.key)
                                    clientsViewModel.getClientsEntrance(
                                        entrance_id,
                                        serviceName.value
                                    )
                                    onExpandedChange(false)
                                }
                                .padding(horizontal = 16.dp)
                        ) {
                            RadioButton(
                                selected = (serviceName.key == selectedOption),
                                onClick = {
                                    onOptionSelected(serviceName.key)
                                    clientsViewModel.getClientsEntrance(
                                        entrance_id,
                                        serviceName.value
                                    )
                                    onExpandedChange(false)
                                }
                            )
                            Text(
                                text = serviceName.key,
                                style = MaterialTheme.typography.body1.merge(),
                                modifier = Modifier.padding(start = 16.dp)
                            )
                        }

                        Divider()
                    }


                }
            }
        }
    }

    private fun navigateToClientsCard(clients: ClientsEntrance) {
        val navController = findNavController()
        navController.navigate(
            ClientsInEntranceComposeDirections.actionClientsInEntranceComposeToClientCardCompose(
                idClient = clients.id_client,
                buildNumber = args.buildNumber,
                streetName = args.streetName,
            )
        )
    }
}