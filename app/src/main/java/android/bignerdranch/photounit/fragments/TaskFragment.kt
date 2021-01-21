package android.bignerdranch.photounit.fragments

import android.bignerdranch.photounit.R
import android.bignerdranch.photounit.model.User
import android.bignerdranch.photounit.model.modelsDB.TaskList
import android.bignerdranch.photounit.utilits.*
import android.bignerdranch.photounit.utilits.viewHolder.ItemViewHolder
import android.bignerdranch.photounit.utilits.viewHolder.SimpleExpandableItemViewHolder
import android.bignerdranch.photounit.viewModels.MultiItemSelectViewModel
import android.bignerdranch.photounit.viewModels.TaskViewModel
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.ItemTouchHelper
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_task.*
import kotlinx.serialization.json.Json
import smartadapter.SmartRecyclerAdapter
import smartadapter.viewevent.listener.OnClickEventListener


class TaskFragment : BaseFragment(R.layout.fragment_task) {

    private val taskViewModel: TaskViewModel by activityViewModels()
    private val multiItemSelectViewModel: MultiItemSelectViewModel by activityViewModels()
    private lateinit var smartRecyclerAdapter: SmartRecyclerAdapter
    private lateinit var twoSmartRecyclerAdapter: SmartRecyclerAdapter
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        detectFirstInput()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        restoreStateRadioButton()
        radioButtonListener()
        getRequestIfChangePositionRadioButton()
        createListenerReceivedTask()
    }

    override fun onPause() {
        saveStateRadioButton()
        super.onPause()
    }

    private fun radioButtonListener() {
        /** Функция слушает переключения RadioButton и изменяет LiveData дня и состояния
         * заявки на выбранные */
        radioGroupData.setOnCheckedChangeListener { _, id_rad_btn ->
            when (id_rad_btn) {
                R.id.r_btn_yesterday -> taskViewModel.selectorDay.value =
                    taskViewModel.getDateTime(YESTERDAY)

                R.id.r_btn_today -> taskViewModel.selectorDay.value =
                    taskViewModel.getDateTime(TODAY)

                R.id.r_btn_tomorrow -> taskViewModel.selectorDay.value =
                    taskViewModel.getDateTime(TOMORROW)
            }
        }

        radioGroupState.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.r_btn_appointed -> taskViewModel.selectorState.value = TASK_APPOINTED
                R.id.r_btn_closed -> taskViewModel.selectorState.value = TASK_CLOSED
            }
        }
    }

    private fun getRequestIfChangePositionRadioButton() {
        /**Функция подключает слушатели к переключателю даты заявки и (назначена/закрыта)
         * и производит запрос данных если эти переключали менются*/

        // Слушатель состояния переключателей даты заявок
        taskViewModel.selectorDay.observe(viewLifecycleOwner, {
            if (taskViewModel.ldUserData.value?.id != null) {

                taskViewModel.httpGetListTask(
                    taskViewModel.mapTask,
                    taskViewModel.ldUserData.value?.id.toString(),  // ID пользователя в базе CRM
                    taskViewModel.selectorState.value!!,
                    taskViewModel.selectorDay.value!!
                )
            }
        })

        // Слушатель состояния переключателей состояния заявки
        taskViewModel.selectorState.observe(viewLifecycleOwner, {
            if (taskViewModel.ldUserData.value?.id != null) {

                taskViewModel.httpGetListTask(
                    taskViewModel.mapTask,
                    taskViewModel.ldUserData.value?.id.toString(),
                    taskViewModel.selectorState.value!!,
                    taskViewModel.selectorDay.value!!
                )
            }
        })
    }

    private fun createListenerReceivedTask() {
        // Показываем RecyclerView как только получили заявки с сервера
        taskViewModel.mapTask.observe(viewLifecycleOwner, { arrayTask ->
            createRecyclerViewAdapter(arrayTask)
        })
    }

    private fun getUserDataFromSharedPref() {
        /**Получаем данные польззователя из SharedPreference*/
        sharedPreferences = requireActivity().getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE)  // Получаем настройки сохраненые в Authorization
        val userData = sharedPreferences.getString(KEY_USER_DATA, null)
        if (userData != null) {
            taskViewModel.ldUserData.value = Json.decodeFromString(User.serializer(), userData)
        } else Toast.makeText(requireContext(), "Ошибка чтения данных пользователя", Toast.LENGTH_SHORT).show()
    }

    private fun createRecyclerViewAdapter(arrayTask: ArrayList<TaskList>) {
        if (taskViewModel.selectorState.value == TASK_APPOINTED) {

            smartRecyclerAdapter = SmartRecyclerAdapter
                .items(arrayTask)
                .map(TaskList::class, ItemViewHolder::class)
                .add(OnClickEventListener {
                    Toast.makeText(requireContext(), "onClick ${it.position}", Toast.LENGTH_SHORT)
                        .show()
                })
                .add(SwipeRemoveItemBinder(ItemTouchHelper.LEFT) {
                    // Remove item from SmartRecyclerAdapter data set
                    if (taskViewModel.selectorState.value == TASK_APPOINTED) {
                        taskViewModel.singleTask.value =
                            arrayTask[it.position] // Помещаем данные выбраной заявки в VM
                        navController.navigate(R.id.action_taskFragment_to_closeTaskFragment)
                        //smartRecyclerAdapter.removeItem(it.viewHolder.adapterPosition)
                    }
                })
                .into(recyclerView)

        } else {

            twoSmartRecyclerAdapter = SmartRecyclerAdapter
                .items(arrayTask)
                .map(TaskList::class, SimpleExpandableItemViewHolder::class)
                .add(multiItemSelectViewModel.observe(this) {
                    mainActivity.toolbar.subtitle =
                        "${multiItemSelectViewModel.viewEventListener.selectedItemsCount} / ${arrayTask.size} всего"
                })
                .into(recyclerView)
        }
    }

    private fun saveStateRadioButton() {
        /**Сохраняем положение RadioButton перед onPause*/
        taskViewModel.radioButtonDay.value = radioGroupData.checkedRadioButtonId
        taskViewModel.radioButtonState.value = radioGroupState.checkedRadioButtonId
    }

    private fun restoreStateRadioButton() {
        /**Восстанавливаем положение RadioButton в onResume*/

        if (taskViewModel.radioButtonDay.value != null) {
            radioGroupData.check(taskViewModel.radioButtonDay.value!!)
        } else {
            radioGroupData.check(R.id.r_btn_today)
        }

        if (taskViewModel.radioButtonState.value != null) {
            radioGroupState.check(taskViewModel.radioButtonState.value!!)
        } else {
            radioGroupState.check(R.id.r_btn_appointed)
        }
    }

    private fun detectFirstInput() {
        if ( taskViewModel.selectorState.value == null) taskViewModel.selectorState.value = TASK_APPOINTED

        //  Если первое включение то выставляем заначения по умолчанию
        if (taskViewModel.selectorDay.value == null)  taskViewModel.selectorDay.value = taskViewModel.getDateTime(TODAY)
        //  Если первый вход и нет данных пользователя получаем их из хранилища
        if (taskViewModel.ldUserData.value == null) {
            getUserDataFromSharedPref()
            taskViewModel.httpGetListTask(taskViewModel.mapTask, taskViewModel.ldUserData.value?.id.toString(), TASK_APPOINTED)
        }
    }
}