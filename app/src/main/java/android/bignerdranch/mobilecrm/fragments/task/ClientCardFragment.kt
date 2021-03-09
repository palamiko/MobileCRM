package android.bignerdranch.mobilecrm.fragments.task

import android.annotation.SuppressLint
import android.bignerdranch.mobilecrm.R
import android.bignerdranch.mobilecrm.databinding.FragmentClientCardBinding
import android.bignerdranch.mobilecrm.fragments.BaseFragment
import android.bignerdranch.mobilecrm.fragments.task.TaskFragment.Companion.KEY_ADDRESS_CLIENT
import android.bignerdranch.mobilecrm.fragments.task.TaskFragment.Companion.KEY_ID_CLIENT
import android.bignerdranch.mobilecrm.fragments.task.TaskFragment.Companion.KEY_PHONE_CLIENT
import android.bignerdranch.mobilecrm.model.modelsDB.ClientCard
import android.bignerdranch.mobilecrm.model.modelsDB.ClientCardBilling
import android.bignerdranch.mobilecrm.utilits.helpers.changeIcon
import android.bignerdranch.mobilecrm.utilits.helpers.returnDateStart
import android.bignerdranch.mobilecrm.utilits.helpers.returnMAC
import android.bignerdranch.mobilecrm.utilits.helpers.translateState
import android.bignerdranch.mobilecrm.viewModels.ClientCardViewModel
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import kotlinx.serialization.ExperimentalSerializationApi


class ClientCardFragment : BaseFragment(R.layout.fragment_client_card) {

    private val clientViewModel: ClientCardViewModel by viewModels()
    var binding: FragmentClientCardBinding? = null


    @ExperimentalSerializationApi
    override fun onStart() {
        super.onStart()
        setObservers()
    }

    @SuppressLint("SetTextI18n")
    @ExperimentalSerializationApi
    private fun setObservers() {
        clientViewModel.clientCard.observe(viewLifecycleOwner){
            initViewFromCRM(it)
            getDataClientCardBilling(it.agreement_number)
        }
        clientViewModel.clientCardBilling.observe(viewLifecycleOwner){
            initViewFromBilling(it)
        }
        clientViewModel.cableTest.observe(viewLifecycleOwner) {
            if (it.state == true) {
                binding?.loadCableTest?.isGone = true
                val re = Regex("[^A-Za-z0-9 ]")
                binding?.tvCableTestValue?.text = "1/2 ${it.pair1}-${re.replace(it.length1!!, "")}   3/6 ${it.pair2}-${re.replace(it.length2!!, "")}"
            } else {
                binding?.loadCableTest?.isGone = true
                Toast.makeText(requireContext(), "Не удачно", Toast.LENGTH_SHORT).show()
            }
        }
        clientViewModel.linkStatus.observe(viewLifecycleOwner) {
            binding?.btnGetLink?.setCompoundDrawablesRelativeWithIntrinsicBounds(
                ContextCompat.getDrawable(requireContext(), changeIcon(it)), null, null, null)
            binding?.loadLinkTest?.isGone = true
            binding?.tvLinkPortValue?.text = it.state
        }
        clientViewModel.countErrors.observe(viewLifecycleOwner) {
            binding?.tvCrcErrorsValue?.text = "CRC: ${it.CRCError}"
            binding?.tvDropErrorsValue?.text = "Drop: ${it.dropError}"
        }
        clientViewModel.speedPort.observe(viewLifecycleOwner) {
            binding?.tvSpeedValue?.text = "${it.speedPort} M/bits"
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
            tvModelSwValue.text = card.model_switch.toString()

            tvLinkPortValue.text     // SwitchInfo
            tvCableTestValue.text    // SwitchInfo
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

        binding?.btnGetLink?.setOnClickListener {
            binding?.btnGetLink?.isVisible = true
            clientViewModel.getLinkStatus()
        }

        binding?.btnErrrors?.setOnClickListener {
            clientViewModel.getCountErrors()
        }

        binding?.btnSpeed?.setOnClickListener {
            clientViewModel.getSpeedPort()
        }
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}