package android.bignerdranch.mobilecrm.ui.composeFun

import android.bignerdranch.mobilecrm.model.modelsDB.ClientCard
import android.bignerdranch.mobilecrm.model.modelsDB.ClientCardBilling
import android.bignerdranch.mobilecrm.model.modelsDB.ClientsEntrance
import android.bignerdranch.mobilecrm.model.modelsDB.Entrance
import android.bignerdranch.mobilecrm.model.viewModels.ClientCardViewModel
import android.bignerdranch.mobilecrm.ui.fragments.task.CardItem
import android.bignerdranch.mobilecrm.ui.fragments.task.returnAddressNode
import android.bignerdranch.mobilecrm.ui.fragments.task.returnSessionInfo
import android.bignerdranch.mobilecrm.ui.fragments.task.translateState
import android.bignerdranch.mobilecrm.ui.theme.SecondaryLightColor
import android.bignerdranch.mobilecrm.utilits.helpers.detectService
import android.bignerdranch.mobilecrm.utilits.helpers.getAddress
import android.bignerdranch.mobilecrm.utilits.helpers.getStateClients
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
                modifier = Modifier.padding( top = 12.dp),
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
                modifier = Modifier.padding( top = 12.dp),
                color = adaptiveColor()
            )
        }
        //Spacer(modifier = Modifier.width(8.dp))
        Column(Modifier.requiredWidth(100.dp)) {
            Text(text = detectService(service),
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
            Text(text = client.numberFloor.toString(),
                modifier = Modifier.padding(top = 12.dp),
                color = adaptiveColor()
            )
        }
    }
}

@Composable
fun BlockInternet(client: ClientCard?, billingCard: ClientCardBilling?, nameStreet: String, buildNumber: String) {
    Column(
        modifier = Modifier.padding(start = 8.dp, end = 8.dp)
    ) {
        CardItem(itemName = ClientCardViewModel.itemsCard[0], value = getAddress(client, nameStreet, buildNumber))
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
fun BlockTv(client: ClientCard?, nameStreet: String, buildNumber: String) {
    Column(
        modifier = Modifier.padding(start = 8.dp, end = 8.dp)
    ) {
        CardItem(itemName = ClientCardViewModel.itemsCard[0], value = "$nameStreet ${buildNumber}-${client?.flat}")
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
