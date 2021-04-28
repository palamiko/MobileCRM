package android.bignerdranch.mobilecrm.ui.fragments.task

import android.bignerdranch.mobilecrm.model.viewModels.ClientCardViewModel
import android.bignerdranch.mobilecrm.ui.theme.MyTestComposeTheme
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.fragment.navArgs
import kotlinx.serialization.ExperimentalSerializationApi


class DetailsHistoryTask : Fragment() {

    private val args: DetailsHistoryTaskArgs by navArgs()

    @ExperimentalSerializationApi
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return ComposeView(requireContext()).apply {
            setContent {
                DetailsHistoryTaskFragment(id_task = args.idTask)

            }
        }
    }
}

@ExperimentalSerializationApi
@Composable
fun DetailsHistoryTaskFragment(id_task: String) {
    MyTestComposeTheme {
        DetailsTask(id_task = id_task)
    }
}

@ExperimentalSerializationApi
@Composable
fun DetailsTask(clientCardViewModel: ClientCardViewModel = viewModel(), id_task: String) {
    val historyTaskList by clientCardViewModel.actionTaskList.observeAsState()
    clientCardViewModel.getActionForTask(id_task)


    LazyColumn(Modifier.padding(8.dp)) {
        items(historyTaskList ?: listOf()) { item ->
            Spacer(modifier = Modifier.height(16.dp))

            Row(Modifier.fillMaxWidth()) {

                Text(text = "Дата: ", color = MaterialTheme.colors.secondaryVariant)
                Text(
                    text = item.date.substringBeforeLast("T"),
                    color = if (isSystemInDarkTheme()) Color.White else Color.Black
                )
            }

            Row(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth()
            ) {
                Text(text = "Комментарий: ", color = MaterialTheme.colors.secondaryVariant)
                Text(
                    text = item.comments,
                    color = if (isSystemInDarkTheme()) Color.White else Color.Black
                )
            }

            Row(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth()
            ) {
                Text(text = "Имя: ", color = MaterialTheme.colors.secondaryVariant)
                Text(
                    text = item.name_master,
                    color = if (isSystemInDarkTheme()) Color.White else Color.Black
                )
            }

            Row(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth()
            ) {
                Text(text = "Сумма: ", color = MaterialTheme.colors.secondaryVariant)
                Text(
                    text = item.summ ?: "-",
                    color = if (isSystemInDarkTheme()) Color.White else Color.Black
                )
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
}

