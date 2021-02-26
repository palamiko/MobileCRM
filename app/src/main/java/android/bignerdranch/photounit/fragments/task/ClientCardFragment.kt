package android.bignerdranch.photounit.fragments.task

import android.annotation.SuppressLint
import android.bignerdranch.photounit.R
import android.bignerdranch.photounit.databinding.FragmentClientCardBinding
import android.bignerdranch.photounit.fragments.BaseFragment
import android.bignerdranch.photounit.fragments.task.TaskFragment.Companion.KEY_ADDRESS_CLIENT
import android.bignerdranch.photounit.fragments.task.TaskFragment.Companion.KEY_ID_CLIENT
import android.bignerdranch.photounit.fragments.task.TaskFragment.Companion.KEY_PHONE_CLIENT
import android.bignerdranch.photounit.model.modelsDB.ClientCard
import android.bignerdranch.photounit.model.modelsDB.ClientCardBilling
import android.bignerdranch.photounit.viewModels.ClientCardViewModel
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import kotlinx.serialization.ExperimentalSerializationApi


class ClientCardFragment : BaseFragment(R.layout.fragment_client_card) {

    private val clientViewModel: ClientCardViewModel by viewModels()
    var binding: FragmentClientCardBinding? = null


    @SuppressLint("SetTextI18n")
    @ExperimentalSerializationApi
    override fun onStart() {
        super.onStart()

        clientViewModel.clientCard.observe(viewLifecycleOwner){
            initViewFromCRM(it)
            getDataClientCardBilling(it.agreement_number)
        }
        clientViewModel.clientCardBilling.observe(viewLifecycleOwner){
            initViewFromBilling(it)
        }
        clientViewModel.cableTest.observe(viewLifecycleOwner) {
            //println(it)
            if (it.state == true) {
                binding?.loadCableTest?.isGone = true
                val re = Regex("[^A-Za-z0-9 ]")
                binding?.tvCableTestValue?.text = "1/2 ${it.pair1}-${re.replace(it.length1!!, "")}   3/6 ${it.pair2}-${re.replace(it.length2!!, "")}"
            } else {
                binding?.loadCableTest?.isGone = true
                Toast.makeText(requireContext(), "Не удачно", Toast.LENGTH_SHORT).show()
            }
        }

    }

    @ExperimentalSerializationApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val idClient: String = requireArguments().getString(KEY_ID_CLIENT).toString()
        val fragmentClientBinding = FragmentClientCardBinding.bind(view)
        binding = fragmentClientBinding
        initButton()
        getDataClientCard(idClient)
    }


    @ExperimentalSerializationApi
    private fun getDataClientCard(id_client: String?) {
        if (id_client != null) {
            clientViewModel.getClientCardCRM(id_client)
        }
    }

    @ExperimentalSerializationApi
    private fun getDataClientCardBilling(numberContract: String?) {
        if (numberContract != null) {
            clientViewModel.getClientCardBilling(numberContract)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initViewFromCRM(card: ClientCard) {
        /**Функция заполняет вьюшки данными с CRM*/
        val clientAddress: String = requireArguments().getString(KEY_ADDRESS_CLIENT).toString()
        val clientPhone: String = requireArguments().getString(KEY_PHONE_CLIENT).toString()

        binding?.apply {
            tvAddressValue.text = clientAddress
            tvPhoneClientCardValue.text = clientPhone
            tvNumberAgreementValue.text = card.agreement_number.toString()
            tvNumberPortValue.text = card.number_port.toString()
            tvNodeValue.text = "${card.street_name} ${card.building_number} д. ${card.entrance_node} п. ${card.flor_node} эт."

            tvLinkPortValue.text     // SwitchInfo
            tvCableTestValue.text    // SwitchInfo
            tvErrorPortValue.text    // SwitchInfo
            tvMacAddressValue.text   // SwitchInfo
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initViewFromBilling(card:ClientCardBilling) {
        /**Функция заполняет вьюшки данными с биллинга*/
        binding?.apply {
            tvStateAccountValue.text = translateState(card.stateAccount)
            tvBalanceValue.text = card.balance
            tvTariffValue.text = card.tariff
            tvSessionValue.text = "${card.ip}${returnDateStart(card)}${returnMAC(card)}"
        }
    }

    @ExperimentalSerializationApi
    private fun initButton() {
        binding?.btnCableTest?.setOnClickListener {
            binding?.loadCableTest?.isVisible = true
            clientViewModel.getCableTest()
        }
        binding?.btnGetError?.setOnClickListener { showToast(getString(R.string.text_for_money)) }
        binding?.btnGetLink?.setOnClickListener { showToast(getString(R.string.text_for_money)) }
        binding?.btnGetMac?.setOnClickListener { showToast(getString(R.string.text_for_money)) }
    }

    private fun returnDateStart(card: ClientCardBilling): String {
        return if (card.dateStart != "") "\n${card.dateStart}" else ""
    }
    private fun returnMAC(card: ClientCardBilling): String {
        return if (card.mac != "") "\n${card.mac}" else ""
    }
    private fun translateState(state: String?): String {
        return when (state) {
            "4" ->  "Недостаточно средств"
            "0" ->  "Активна"
            "10" -> "Отключена"
            else -> "Ошибка"
        }
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}