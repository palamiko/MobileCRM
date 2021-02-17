package android.bignerdranch.photounit.fragments.task

import android.bignerdranch.photounit.R
import android.bignerdranch.photounit.databinding.FragmentCloseTaskBinding
import android.bignerdranch.photounit.fragments.BaseFragment
import android.bignerdranch.photounit.model.MaterialUsed
import android.bignerdranch.photounit.model.modelsDB.TaskModel
import android.bignerdranch.photounit.utilits.SwipeRemoveItemBinder
import android.bignerdranch.photounit.utilits.helpers.MyTextWatcher
import android.bignerdranch.photounit.utilits.hideKeyboard
import android.bignerdranch.photounit.utilits.showKeyboard
import android.bignerdranch.photounit.utilits.viewHolder.ItemViewHolderLite
import android.bignerdranch.photounit.viewModels.TaskViewModel
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import smartadapter.SmartRecyclerAdapter
import smartadapter.viewevent.listener.OnClickEventListener


class CloseTaskFragment : BaseFragment(R.layout.fragment_close_task) {

    private val taskVM: TaskViewModel by activityViewModels()
    private var binding: FragmentCloseTaskBinding? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fragmentBinding = FragmentCloseTaskBinding.bind(view)
        binding = fragmentBinding

        binding!!.teCommentClose.requestFocus()
        view.showKeyboard()
    }

    @ExperimentalSerializationApi
    override fun onStart() {
        super.onStart()
        initView()
    }

    override fun onResume() {
        super.onResume()
        restoreTextFromEditText()
        createRecyclerViewAdapter(taskVM.selectMaterial.value!!)
    }

    override fun onPause() {
        super.onPause()
        view?.hideKeyboard()  // Скрыть клавиатуру при выходе с экрана
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        // Сохряняем заполенные поля
        if (binding?.teCommentClose?.text != null) {
            outState.putString(TEXT_COMMENT, binding?.teCommentClose?.text.toString())
        }
        if (binding?.teSumm?.text != null) {
            outState.putString(TEXT_SUMM, binding?.teSumm?.text.toString())
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)

        if (savedInstanceState != null) {
            binding?.teCommentClose?.setText(savedInstanceState.getString(TEXT_COMMENT, ""))
            binding?.teSumm?.setText(savedInstanceState.getString(TEXT_SUMM, ""))
        }
    }

    private fun detectAddress(item: TaskModel?): String {
        /**Эта ф-ия проверяет приходит ли заявка с полным адресом или адрес забит от руки
         * и возвращает разные вариации в поле адрес*/
        return if (item?.name_ru == null) {
            item!!.address!!
        } else "${item.name_ru} ${item.building_number}-${item.flat}"
    }

    @ExperimentalSerializationApi
    private fun initView() {
        binding?.tvAddressCloseTask?.text = detectAddress(taskVM.singleTask.value!!)
        binding?.tvCommentCloseTask?.text = taskVM.singleTask.value!!.comments

        detectPayTask()
        initializationBtnClickListener()
        setTextEditChangeListener()
    }

    private fun createRecyclerViewAdapter(arrayMaterialSelect: ArrayList<MaterialUsed>) {
        if (taskVM.selectMaterial.value != null ) {
            if (arrayMaterialSelect.isNotEmpty()) binding?.listUsedMaterial?.isVisible = true
            var smartRecyclerAdapter: SmartRecyclerAdapter? = null
            smartRecyclerAdapter = SmartRecyclerAdapter
                .items(arrayMaterialSelect)
                .map(MaterialUsed::class, ItemViewHolderLite::class)
                .add(OnClickEventListener {
                    Toast.makeText(requireContext(), "onClick ${it.position}", Toast.LENGTH_SHORT)
                        .show()
                })
                .add(SwipeRemoveItemBinder(ItemTouchHelper.LEFT) {
                    // Remove item from SmartRecyclerAdapter data set
                    smartRecyclerAdapter?.removeItem(it.viewHolder.adapterPosition)
                    println(taskVM.selectMaterial.value)
                })
                .into(binding?.listUsedMaterial!!)
        }
    }

    @ExperimentalSerializationApi
    private suspend fun closeTask(): String {
        return taskVM.closeTask(getText(binding?.teCommentClose!!), getText(binding?.teSumm!!))

    }

    @ExperimentalSerializationApi
    private fun initializationBtnClickListener() {
        /**Инициализация слушателей нажатий кнопок*/
        binding?.btnCloseTask?.setOnClickListener {
            coroutineScope.launch {
                val messageResult: String = closeTask()
                navigateToTaskFragment(messageResult)
            }
        }

        binding?.btnAddMaterial?.setOnClickListener {
            findNavController().navigate(R.id.action_closeTaskFragment_to_selectMaterialFragment)
        }
    }

    private suspend fun navigateToTaskFragment(message: String) = withContext(Dispatchers.Main) {
        /**Функция производит навигацую к TaskFragment и передает туда результат закрытия заявки*/
        val bundle  = bundleOf(KEY_RESULT_CLOSE to message, KEY_IS_CLOSE_FRAGMENT to true)  // Ложим результат в Bundle
        findNavController().navigate(R.id.action_closeTaskFragment_to_taskFragment, bundle)
    }

    private fun detectPayTask() {
        /**Функция определяет платная заявка или нет и меняет надпись в RecyclerView*/
        if (!taskVM.singleTask.value!!.ispayable!!) {
            binding?.teSumm?.isInvisible = true
        }
        if (taskVM.singleTask.value!!.isDom!!) {
            binding?.teSumm?.isVisible = true
        }
    }

    private fun setTextEditChangeListener() {
        /**Сохраняет при изменении значения полей TextEdit, коментарии и смму*/
        binding?.teCommentClose?.addTextChangedListener(object : MyTextWatcher() {
            override fun afterTextChanged(s: Editable?) {
                taskVM.savedComment = s.toString()
            }
        })

        binding?.teSumm?.addTextChangedListener(object : MyTextWatcher() {
            override fun afterTextChanged(s: Editable?) {
                taskVM.savedSumm = s.toString()
            }
        })
    }

    private fun restoreTextFromEditText() {
        /**Восстанавливает значения полей TextEdit, коментарии и смму*/
        if (taskVM.savedComment != null) binding?.teCommentClose?.setText(taskVM.savedComment)
        if (taskVM.savedSumm != null) binding?.teSumm?.setText(taskVM.savedSumm)
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    companion object {
        const val TEXT_COMMENT = "text_edit_comment"
        const val TEXT_SUMM = "text_edit_summ"
        const val KEY_RESULT_CLOSE = "result_close"
        private const val KEY_IS_CLOSE_FRAGMENT = "from_close_fragment"
    }
}
