package android.bignerdranch.mobilecrm.ui.fragments.task

import android.bignerdranch.mobilecrm.model.viewModels.ClientCardViewModel
import android.bignerdranch.mobilecrm.ui.composeFun.*
import android.bignerdranch.mobilecrm.ui.theme.LightPrimaryLightColor
import android.bignerdranch.mobilecrm.ui.theme.MyTestComposeTheme
import android.bignerdranch.mobilecrm.ui.theme.SecondaryDarkColor
import android.bignerdranch.mobilecrm.ui.theme.SecondaryLightColor
import android.bignerdranch.mobilecrm.utilits.helpers.detectArgType
import android.bignerdranch.mobilecrm.utilits.helpers.getAddress
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import kotlinx.serialization.ExperimentalSerializationApi


class ComposeClientCard : Fragment() {
    private val args: ComposeClientCardArgs by navArgs()

    @ExperimentalSerializationApi
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val lifecycleOwner = viewLifecycleOwner
        val navController = findNavController()

        return ComposeView(requireContext()).apply {
            setContent {
                ComposeFragment {
                    ClientCardView (
                        args = args,
                        lifecycleOwner = lifecycleOwner,
                        navController = navController,
                        graphName = "taskGraph"
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
    args: Any,
    lifecycleOwner: LifecycleOwner,
    navController: NavController,
    graphName: String
) {
    val crmCard by clientCardViewModel.clientCard.observeAsState()
    val billingCard by clientCardViewModel.clientCardBilling.observeAsState()

    val resultCableTest by clientCardViewModel.cableTest.observeAsState()
    val resultLinkStatus by clientCardViewModel.linkStatus.observeAsState()
    val resultCountError by clientCardViewModel.countErrors.observeAsState()
    val resultSpeedPort by clientCardViewModel.speedPort.observeAsState()
    val historyTaskList by clientCardViewModel.historyTaskList.observeAsState()
    val arg = detectArgType(args)  // Возвращаем универсалныйе аргументы для двух фрагментов




    // Запрос данных CRM
    clientCardViewModel.getClientCardCRM(arg.id_client.toString())

    // Слушает ClientCard, если абон интернет то запрашивает данные из биллинга
    clientCardViewModel.clientCard.observe(lifecycleOwner) {
        clientCardViewModel.getHistoryTaskList()  // Запрос истории заявок

        if (it.is_internet) {
            clientCardViewModel.getClientCardBilling(it.agreement_number)
        }
    }


    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {

        /** Поля данных абонента */
        item {
            when {
                crmCard?.is_internet == true -> {
                    BlockInternet(
                        client = crmCard,
                        billingCard = billingCard,
                        nameStreet = arg.nameStreet,
                        buildNumber = arg.buildNumber,
                        fullAddress = arg.fullAddress
                    )
                }

                crmCard?.is_tv == true -> {
                    BlockTv(
                        client = crmCard,
                        nameStreet = arg.nameStreet,
                        buildNumber = arg.buildNumber,
                        fullAddress = arg.fullAddress
                    )

                }
                crmCard?.is_domofon == true -> {
                    BlockTv(
                        client = crmCard,
                        nameStreet = arg.nameStreet,
                        buildNumber = arg.buildNumber,
                        fullAddress = arg.fullAddress
                    )
                }
            }
        }

        /** Заголовок раздела инструменты */
        item { if (crmCard?.is_internet == true) TitlePart("Инструменты") }

        /** Тело раздела инструменты */

        item {
            if (crmCard?.is_internet == true) {
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
        }
        /** Заголовок истории заявок */
        item { TitlePart("История заявок") }


        /** Блок истории заявок */
        item {
            HistoryTaskBlock(
                historyTaskList = historyTaskList,
                navController = navController,
                address = arg.fullAddress ?: getAddress(client = crmCard, arg.nameStreet, arg.buildNumber),
                navTo = graphName
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
