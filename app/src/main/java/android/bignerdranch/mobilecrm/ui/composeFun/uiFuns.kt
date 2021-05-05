package android.bignerdranch.mobilecrm.ui.composeFun

import android.bignerdranch.mobilecrm.model.modelsDB.ClientsEntrance
import android.bignerdranch.mobilecrm.model.modelsDB.Entrance
import android.bignerdranch.mobilecrm.utilits.helpers.detectService
import android.bignerdranch.mobilecrm.utilits.helpers.getStateClients
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
fun adaptiveColor(): Color = if (isSystemInDarkTheme()) Color.White else Color.Black
