package android.bignerdranch.mobilecrm.ui.fragments.clients

import android.bignerdranch.mobilecrm.model.modelsDB.districtArray
import android.bignerdranch.mobilecrm.model.modelsDB.districtMap
import android.bignerdranch.mobilecrm.ui.composeFun.SmallListItem
import android.bignerdranch.mobilecrm.ui.theme.MyTestComposeTheme
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import kotlinx.serialization.ExperimentalSerializationApi


class DistrictsCompose : Fragment() {

    @ExperimentalSerializationApi
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                ComposeFragment {
                    DistrictList()
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

    @Preview
    @Composable
    fun DistrictList() {
        /** Список районов */

        LazyColumn {
            items(districtArray) { item ->
                ItemDistrict(item = item)
            }
        }
    }

    @Composable
    fun ItemDistrict(item: String) {
        /** Один элемент списка */

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .requiredHeight(44.dp)
                .clickable {
                    val id = getDistrictId(item)
                    navigateToStreets(id)
                }
        ) { SmallListItem(text = item) }
    }

    private fun navigateToStreets(district_id: Int) {
        val navController = findNavController()
        navController.navigate(
            DistrictsComposeDirections.actionDistrictsComposeToStreetsCompose(
                districtId = district_id
            )
        )
    }

    private fun getDistrictId(districtName: String): Int = districtMap.getValue(districtName)
}