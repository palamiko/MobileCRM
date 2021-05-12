package android.bignerdranch.mobilecrm.ui.fragments.clients

import android.bignerdranch.mobilecrm.model.viewModels.ClientCardViewModel
import android.bignerdranch.mobilecrm.ui.composeFun.BlockInternet
import android.bignerdranch.mobilecrm.ui.composeFun.BlockTv
import android.bignerdranch.mobilecrm.ui.composeFun.TitlePart
import android.bignerdranch.mobilecrm.ui.fragments.task.HistoryTaskBlock
import android.bignerdranch.mobilecrm.ui.fragments.task.ToolsItemsPart
import android.bignerdranch.mobilecrm.ui.theme.MyTestComposeTheme
import android.bignerdranch.mobilecrm.utilits.helpers.getAddress
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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


class ClientCardCompose : Fragment() {
    private val args: ClientCardComposeArgs by navArgs()

    @ExperimentalSerializationApi
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                ComposeFragment() {
                    ClientsCard()
                }
            }
        }
    }

    @ExperimentalSerializationApi
    @Composable
    fun ComposeFragment(
        container: @Composable () -> Unit
    ) {
        MyTestComposeTheme {
            container()
        }
    }

    @ExperimentalSerializationApi
    @Composable
    fun ClientsCard(clientCardViewModel: ClientCardViewModel = viewModel()) {

        val clientCard by clientCardViewModel.clientCard.observeAsState()
        val clientBillingCard by clientCardViewModel.clientCardBilling.observeAsState()

        val resultCableTest by clientCardViewModel.cableTest.observeAsState()
        val resultLinkStatus by clientCardViewModel.linkStatus.observeAsState()
        val resultCountError by clientCardViewModel.countErrors.observeAsState()
        val resultSpeedPort by clientCardViewModel.speedPort.observeAsState()
        val historyTaskList by clientCardViewModel.historyTaskList.observeAsState()

        // Запрос данных CRM
        clientCardViewModel.getClientCardCRM(args.idClient.toString())

        // Слушает ClientCard, если абон интернет то запрашивает данные из биллинга
        clientCardViewModel.clientCard.observe(viewLifecycleOwner) {
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
                    clientCard?.is_internet == true -> {
                        BlockInternet(client = clientCard, billingCard = clientBillingCard, args.streetName, args.buildNumber)

                    }
                    clientCard?.is_tv == true -> {
                        BlockTv(client = clientCard, nameStreet = args.streetName, buildNumber = args.buildNumber)

                    }
                    clientCard?.is_domofon == true -> {
                        BlockTv(client = clientCard, nameStreet = args.streetName, buildNumber = args.buildNumber)
                    }
                }
            }

            /** Заголовок раздела инструменты */
            item { if (clientCard?.is_internet == true) TitlePart("Инструменты") }

            /** Тело раздела инструменты */
            item {
                if (clientCard?.is_internet == true) {
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
                    navController = findNavController(),
                    address = getAddress(client = clientCard, args.streetName, args.buildNumber)
                )
            }
        }
    }
}







