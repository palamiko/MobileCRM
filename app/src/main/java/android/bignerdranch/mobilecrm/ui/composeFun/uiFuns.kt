package android.bignerdranch.mobilecrm.ui.composeFun

import android.bignerdranch.mobilecrm.model.modelsDB.*
import android.bignerdranch.mobilecrm.model.networkModel.ResultCableTest
import android.bignerdranch.mobilecrm.model.networkModel.ResultErrorTest
import android.bignerdranch.mobilecrm.model.networkModel.ResultLinkStatus
import android.bignerdranch.mobilecrm.model.networkModel.ResultSpeedPort
import android.bignerdranch.mobilecrm.model.viewModels.ClientCardViewModel
import android.bignerdranch.mobilecrm.ui.fragments.task.CardItem
import android.bignerdranch.mobilecrm.ui.theme.SecondaryLightColor
import android.bignerdranch.mobilecrm.utilits.helpers.*
import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.serialization.ExperimentalSerializationApi
import java.util.*

@Composable
fun SmallListItem(text: String) {

    Divider(
        thickness = 1.dp,
        modifier = Modifier.fillMaxWidth(),
        color = Color.Gray
    )
    Text(
        modifier = Modifier.padding(start = 16.dp, top = 12.dp),
        fontSize = 16.sp,
        text = text,
        color = adaptiveColor()
    )
}

@Composable
fun EntranceListItem(entrance: Entrance) {
    /** Один item из списка подъездов */

    Divider(
        thickness = 1.dp,
        modifier = Modifier.fillMaxWidth(),
        color = Color.Gray
    )
    Row(Modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.width(32.dp))

        // Номер подъезда
        Column(Modifier.requiredWidth(28.dp)) {
            Text(
                text = entrance.number,
                modifier = Modifier.padding(top = 12.dp),
                color = adaptiveColor()
            )
        }

        Spacer(modifier = Modifier.width(32.dp))

        // Где узел для данного подъезда
        Column() {
            Text(
                text = "${entrance.nodeEntrance} п.",
                modifier = Modifier.padding(top = 12.dp),
                color = adaptiveColor()
            )
        }

        // Горизонт отступ
        Spacer(modifier = Modifier.width(32.dp))

        // Коментарий подъезда
        Column(Modifier.requiredWidth(160.dp)) {
            Text(
                text = entrance.comment,
                modifier = Modifier.padding(top = 12.dp),
                color = adaptiveColor()
            )
        }
    }
}

@Composable
fun ClientListItem(client: ClientsEntrance) {
    val service = mapOf(
        "Интернет" to client.internet,
        "Домофон" to client.domofon,
        "Телевидение" to client.tv
    )

    Divider(
        thickness = 1.dp,
        modifier = Modifier.fillMaxWidth(),
        color = Color.Gray
    )
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        //Spacer(modifier = Modifier.width(16.dp))
        Column(Modifier.requiredWidth(27.dp)) {
            Text(
                text = client.numberFlat.toString(),
                modifier = Modifier.padding(top = 12.dp),
                color = adaptiveColor()
            )
        }
        //Spacer(modifier = Modifier.width(8.dp))
        Column(Modifier.requiredWidth(100.dp)) {
            Text(
                text = detectService(service),
                modifier = Modifier.padding(top = 12.dp),
                color = adaptiveColor()
            )
        }
        //Spacer(modifier = Modifier.width(8.dp))
        Column(Modifier.requiredWidth(108.dp)) {
            Text(
                text = getStateClients(client),
                modifier = Modifier.padding(top = 12.dp),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = adaptiveColor()
            )
        }
        //Spacer(modifier = Modifier.width(8.dp))
        Column(Modifier.requiredWidth(32.dp)) {
            Text(
                text = client.numberFloor.toString(),
                modifier = Modifier.padding(top = 12.dp),
                color = adaptiveColor()
            )
        }
    }
}

@Composable
fun BlockInternet(
    client: ClientCard?,
    billingCard: ClientCardBilling?,
    nameStreet: String?,
    buildNumber: String?,
    fullAddress: String?
) {
    Column(
        modifier = Modifier.padding(start = 8.dp, end = 8.dp)
    ) {
        CardItem(
            itemName = ClientCardViewModel.itemsCard[0],
            value = fullAddress ?: getAddress(client, nameStreet, buildNumber)
        )
        CardItem(itemName = ClientCardViewModel.itemsCard[1], client?.phones ?: "-")
        CardItem(itemName = ClientCardViewModel.itemsCard[2], client?.agreement_number.toString())
        CardItem(itemName = ClientCardViewModel.itemsCard[3], translateState(billingCard))
        CardItem(itemName = ClientCardViewModel.itemsCard[4], billingCard?.balance.toString())
        CardItem(itemName = ClientCardViewModel.itemsCard[5], billingCard?.tariff.toString())
        CardItem(itemName = ClientCardViewModel.itemsCard[6], returnSessionInfo(billingCard))
        CardItem(itemName = ClientCardViewModel.itemsCard[7], client?.number_port ?: "Не выбран")
        CardItem(itemName = ClientCardViewModel.itemsCard[8], client?.model_switch ?: "Не выбран")
        CardItem(
            itemName = ClientCardViewModel.itemsCard[9],
            returnAddressNode(client).toLowerCase(Locale.ROOT).capitalize(Locale.ROOT)
        )
    }
}

@Composable
fun BlockTv(client: ClientCard?, nameStreet: String?, buildNumber: String?, fullAddress: String?) {
    Column(
        modifier = Modifier.padding(start = 8.dp, end = 8.dp)
    ) {
        CardItem(
            itemName = ClientCardViewModel.itemsCard[0],
            value = fullAddress ?: getAddress(client, nameStreet, buildNumber)
        )
        CardItem(itemName = ClientCardViewModel.itemsCard[1], client?.phones ?: "-")
        CardItem(itemName = ClientCardViewModel.itemsCard[2], client?.agreement_number.toString())
        //CardItem(itemName = ClientCardViewModel.itemsCard[7], client?.number_port.toString())
        //CardItem(itemName = ClientCardViewModel.itemsCard[8], client?.model_switch.toString())
    }
}

@Composable
fun TitlePart(title: String) {
    Column(
        modifier = Modifier
            .padding(top = 16.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            color = if (isSystemInDarkTheme()) SecondaryLightColor else MaterialTheme.colors.secondaryVariant,
            style = MaterialTheme.typography.h6
        )
    }
}

@Composable
fun adaptiveColor(): Color = if (isSystemInDarkTheme()) Color.White else Color.Black

@ExperimentalSerializationApi
@Composable
fun ToolsItemsPart(
    /** Бок значений полученных от утилит */

    resultCableTest: ResultCableTest?,
    resultLinkStatus: ResultLinkStatus?,
    resultCountError: ResultErrorTest?,
    resultSpeedPort: ResultSpeedPort?,
    cardViewModel: ClientCardViewModel,
    context: Context = LocalContext.current,
    modifier: Modifier,
    modifierButton: Modifier = Modifier.width(113.dp)
) {
    Column(Modifier.padding(start = 8.dp, top = 8.dp, end = 8.dp)) {


        Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier) {
            Button(modifier = modifierButton,
                onClick = { cardViewModel.getCableTest() }
            ) {
                Text(text = "Cab Test")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = returnCabTestString(resultCableTest, context),
                color = if (isSystemInDarkTheme()) Color.White else Color.Black,
            )

        }

        Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier) {
            Button(modifier = modifierButton,
                onClick = { cardViewModel.getLinkStatus() }
            ) {
                Text(text = "Link")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = resultLinkStatus?.state ?: "",
                color = if (isSystemInDarkTheme()) Color.White else Color.Black
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier) {
            Button(modifier = modifierButton,
                onClick = { cardViewModel.getSpeedPort() }
            ) {
                Text(text = "PortSpeed")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (resultSpeedPort?.speedPort != null) "${resultSpeedPort.speedPort} M/bits" else "",
                color = if (isSystemInDarkTheme()) Color.White else Color.Black
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier) {
            Button(modifier = modifierButton,
                onClick = { cardViewModel.getCountErrors() }
            ) {
                Text(text = "Errors")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = returnErrorPortsString(resultCountError),
                color = if (isSystemInDarkTheme()) Color.White else Color.Black
            )
        }
    }
}

@Composable
fun HistoryTaskBlock(
    historyTaskList: List<HistoryTask>?,
    navController: NavController,
    address: String,
    navTo: String
) {
    Column(
        Modifier
            .padding(start = 8.dp, top = 8.dp, end = 8.dp)
    ) {
        if (historyTaskList != null) {
            for (task in historyTaskList) {
                Box(Modifier.clickable(onClick = {

                    if (navTo == "taskGraph") {
                        navigateToDetailsHistoryTask(navController = navController, task = task)
                    } else if (navTo == "clientGraph") {
                        navigateToClientHistoryTask(navController = navController, task = task)
                    }

                })) {
                    HistoryItem(
                        state = task.state_task,
                        address = address.toLowerCase(Locale.ROOT).capitalize(Locale.ROOT),
                        comment = task.comments,
                        dateCreate = task.dateCreate.substringBeforeLast("T"),
                        nameMaster = task.name_master,
                        dateEnd = task.dateEnded.substringBeforeLast("T"),
                        feedback = task.feedback
                    )
                }
            }
        }
    }
}


@Composable
fun HistoryItem(
    state: String,
    address: String,
    comment: String,
    dateCreate: String,
    nameMaster: String,
    dateEnd: String,
    feedback: String
) {
    Column {
        Row {
            Text(
                text = "Статус: ",
                color = if (isSystemInDarkTheme()) SecondaryLightColor else MaterialTheme.colors.secondaryVariant
            )
            Text(text = state, color = if (isSystemInDarkTheme()) Color.White else Color.Black)
        }
        Row {
            Text(
                text = "Адрес: ",
                color = if (isSystemInDarkTheme()) SecondaryLightColor else MaterialTheme.colors.secondaryVariant
            )
            Text(text = address, color = if (isSystemInDarkTheme()) Color.White else Color.Black)
        }
        Row {
            Text(
                text = "Создана: ",
                color = if (isSystemInDarkTheme()) SecondaryLightColor else MaterialTheme.colors.secondaryVariant
            )
            Text(text = dateCreate, color = if (isSystemInDarkTheme()) Color.White else Color.Black)
        }
        Row {
            Text(
                text = "Назначена на: ",
                color = if (isSystemInDarkTheme()) SecondaryLightColor else MaterialTheme.colors.secondaryVariant
            )
            Text(text = nameMaster, color = if (isSystemInDarkTheme()) Color.White else Color.Black)
        }
        Row {
            Text(
                text = "Комментарий: ",
                color = if (isSystemInDarkTheme()) SecondaryLightColor else MaterialTheme.colors.secondaryVariant
            )
            Text(
                text = comment,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                color = if (isSystemInDarkTheme()) Color.White else Color.Black
            )
        }

        Row {
            Text(
                text = "Завершена: ",
                color = if (isSystemInDarkTheme()) SecondaryLightColor else MaterialTheme.colors.secondaryVariant
            )
            Text(text = dateEnd, color = if (isSystemInDarkTheme()) Color.White else Color.Black)
        }
        Row {
            Text(
                text = "Обратная связь: ",
                color = if (isSystemInDarkTheme()) SecondaryLightColor else MaterialTheme.colors.secondaryVariant
            )
            Text(text = feedback, color = if (isSystemInDarkTheme()) Color.White else Color.Black)
        }
        Divider(
            thickness = 2.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, end = 8.dp),
            color = Color.Gray
        )
    }
}
