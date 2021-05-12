package android.bignerdranch.mobilecrm.ui.fragments.task

import android.bignerdranch.mobilecrm.model.modelsDB.ClientCard
import android.bignerdranch.mobilecrm.model.modelsDB.ClientCardBilling
import android.bignerdranch.mobilecrm.model.modelsDB.HistoryTask
import android.bignerdranch.mobilecrm.model.networkModel.ResultCableTest
import android.bignerdranch.mobilecrm.model.networkModel.ResultErrorTest
import android.bignerdranch.mobilecrm.model.networkModel.ResultLinkStatus
import android.bignerdranch.mobilecrm.model.networkModel.ResultSpeedPort
import android.bignerdranch.mobilecrm.model.viewModels.ClientCardViewModel
import android.bignerdranch.mobilecrm.model.viewModels.ClientCardViewModel.Companion.itemsCard
import android.bignerdranch.mobilecrm.ui.theme.LightPrimaryLightColor
import android.bignerdranch.mobilecrm.ui.theme.MyTestComposeTheme
import android.bignerdranch.mobilecrm.ui.theme.SecondaryDarkColor
import android.bignerdranch.mobilecrm.ui.theme.SecondaryLightColor
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import kotlinx.serialization.ExperimentalSerializationApi
import java.util.*


class ComposeClientCard : Fragment() {
    private val args: ComposeClientCardArgs by navArgs()

    @ExperimentalSerializationApi
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val lifecycleOwner = this.viewLifecycleOwner
        val navController = findNavController()
        return ComposeView(requireContext()).apply {
            setContent {
                ComposeFragment {
                    ClientCardView(
                        args = args,
                        lifecycleOwner = lifecycleOwner,
                        navController = navController
                    )
                }
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
fun ClientCardView(
    clientCardViewModel: ClientCardViewModel = viewModel(),
    args: ComposeClientCardArgs,
    lifecycleOwner: LifecycleOwner,
    navController: NavController
) {
    val crmCard by clientCardViewModel.clientCard.observeAsState()
    val billingCard by clientCardViewModel.clientCardBilling.observeAsState()
    val resultCableTest by clientCardViewModel.cableTest.observeAsState()
    val resultLinkStatus by clientCardViewModel.linkStatus.observeAsState()
    val resultCountError by clientCardViewModel.countErrors.observeAsState()
    val resultSpeedPort by clientCardViewModel.speedPort.observeAsState()
    val historyTaskList by clientCardViewModel.historyTaskList.observeAsState()
    val itemsCard = itemsCard

    clientCardViewModel.getClientCardCRM(args.idClient.toString())
    clientCardViewModel.clientCard.observe(lifecycleOwner) {
        clientCardViewModel.getClientCardBilling(it.agreement_number)
        clientCardViewModel.getHistoryTaskList()
    }


    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {

        /** Поля данных абонента */
        item {
            Column(
                modifier = Modifier
                    .padding(start = 8.dp, end = 8.dp)
            ) {
                CardItem(
                    itemName = itemsCard[0],
                    args.addressClient.toLowerCase(Locale.ROOT).capitalize(Locale.ROOT)
                )
                CardItem(itemName = itemsCard[1], args.phoneClient ?: "-")
                CardItem(itemName = itemsCard[2], crmCard?.agreement_number.toString())
                CardItem(itemName = itemsCard[3], translateState(billingCard))
                CardItem(itemName = itemsCard[4], billingCard?.balance.toString())
                CardItem(itemName = itemsCard[5], billingCard?.tariff.toString())
                CardItem(itemName = itemsCard[6], returnSessionInfo(billingCard))
                CardItem(itemName = itemsCard[7], crmCard?.number_port.toString())
                CardItem(itemName = itemsCard[8], crmCard?.model_switch.toString())
                CardItem(
                    itemName = itemsCard[9],
                    returnAddressNode(crmCard).toLowerCase(Locale.ROOT).capitalize(Locale.ROOT)
                )
            }

        }

        /** Заголовок раздела инструменты */
        item {
            Column(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Инструменты",
                    color = if (isSystemInDarkTheme()) SecondaryLightColor else MaterialTheme.colors.secondaryVariant,
                    style = MaterialTheme.typography.h6
                )
            }
        }
        /** Тело раздела инструменты */
        item {
            ToolsItemsPart(
                resultCableTest,
                resultLinkStatus,
                resultCountError,
                resultSpeedPort,
                cardViewModel = clientCardViewModel,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )
        }
        /** Заголовок истории заявок */
        item {
            Column(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "История заявок",
                    color = if (isSystemInDarkTheme()) SecondaryLightColor else MaterialTheme.colors.secondaryVariant,
                    style = MaterialTheme.typography.h6
                )
            }
        }
        /** Блок истории заявок */
        item {
            HistoryTaskBlock(
                historyTaskList = historyTaskList,
                navController = navController,
                address = args.addressClient
            )
        }
    }
}


@Composable
fun CardItem(itemName: String, value: String) {
    /** Одна строка с данными абонента */
    Spacer(modifier = Modifier.height(6.dp))
    Surface(
        color = if (isSystemInDarkTheme()) MaterialTheme.colors.surface else LightPrimaryLightColor,
        border = BorderStroke(1.dp, SecondaryDarkColor),
        shape = RoundedCornerShape(4.dp),
        elevation = 2.dp,
    ) {

        Row(Modifier.fillMaxSize()) {
            Text(
                text = itemName,
                Modifier
                    .padding(start = 4.dp, top = 4.dp, bottom = 4.dp)
                    .width(138.dp),
                color = if (isSystemInDarkTheme()) SecondaryLightColor else MaterialTheme.colors.secondaryVariant

            )
            Text(
                text = value,
                Modifier.padding(top = 4.dp, bottom = 4.dp),
                maxLines = 2,
                //overflow = TextOverflow.Ellipsis,
                color = if (isSystemInDarkTheme()) Color.White else Color.Black
            )
        }
    }
}


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
    address: String
) {
    Column(
        Modifier
            .padding(start = 8.dp, top = 8.dp, end = 8.dp)
    ) {
        if (historyTaskList != null) {
            for (task in historyTaskList) {
                Box(Modifier.clickable(onClick = {
                    navController.navigate(
                        ComposeClientCardDirections.actionComposeClientCardToDetailsHistoryTask(
                            task.id_task
                        )
                    )
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


private fun returnErrorPortsString(resultCountError: ResultErrorTest?): String {
    /** Возвращает форматированую строку с колличеством ошибок */

    return if (resultCountError?.dropError != null) {
        "CRC: ${resultCountError.CRCError} Drop: ${resultCountError.dropError}"
    } else {
        ""
    }
}

private fun returnCabTestString(resultCableTest: ResultCableTest?, context: Context): String {
    /** Возвращает форматированую строку с результатом кабель теста */

    val re = Regex("[^A-Za-z0-9 ]")
    return when (resultCableTest?.state) {
        true -> {
            "1/2 ${resultCableTest.pair1}-${
                re.replace(
                    resultCableTest.length1!!,
                    ""
                )
            }   3/6 ${resultCableTest.pair2}-${re.replace(resultCableTest.length2!!, "")}"
        }
        null -> {
            ""
        }
        else -> {
            Toast.makeText(context, "Не удачно", Toast.LENGTH_SHORT).show()
            "Ошибка.."
        }
    }
}

fun returnAddressNode(card: ClientCard?): String {
    /** Возвращает форматированую стоку с адресом узла */

    return if (card?.street_name != null) {
        "${card.street_name} ${card.building_number} д. ${card.entrance_node} п. ${card.flor_node} эт."
    } else {
        "Не выбран"
    }

}

fun returnSessionInfo(card: ClientCardBilling?): String {
    /** Возвращает форматированую строку с информацией о сессии */
    return "${card?.ip}${returnDateStart(card)}${returnMAC(card)}"
}

private fun returnDateStart(card: ClientCardBilling?): String {
    return if (card?.dateStart != "") "\n${card?.dateStart}" else ""
}

private fun returnMAC(card: ClientCardBilling?): String {
    return if (card?.mac != "") "\n${card?.mac}" else ""
}

fun translateState(card: ClientCardBilling?): String {
    return when (card?.stateAccount.toString()
    ) {
        "4" -> "Недостаточно средств"
        "0" -> "Активна"
        "10" -> "Отключена"
        else -> "Ошибка"
    }
}




