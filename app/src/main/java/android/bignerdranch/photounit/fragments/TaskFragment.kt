package android.bignerdranch.photounit.fragments

import android.bignerdranch.photounit.R
import android.bignerdranch.photounit.activity.MainActivity
import android.bignerdranch.photounit.databinding.FragmentTaskBinding
import android.bignerdranch.photounit.model.User
import android.bignerdranch.photounit.model.modelsDB.TaskList
import android.bignerdranch.photounit.utilits.*
import android.bignerdranch.photounit.utilits.viewHolder.MainAdapter
import android.bignerdranch.photounit.viewModels.TaskViewModel
import android.bignerdranch.photounit.viewModels.UserViewModel
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json


class TaskFragment : Fragment() {

    private val taskViewModel: TaskViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()

    private var binding: FragmentTaskBinding? = null

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var savedStateHandle: SavedStateHandle

    private lateinit var viewAdapter: RecyclerView.Adapter<*>


    private lateinit var observerSelectorDay: Observer<String>
    private lateinit var observerSelectorState: Observer<Char>
    private lateinit var observerArrayTask: Observer<ArrayList<TaskList>>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val navController = findNavController()
        val currentBackStackEntry = navController.currentBackStackEntry!!
        savedStateHandle = currentBackStackEntry.savedStateHandle

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val fragmentBinding = FragmentTaskBinding.inflate(inflater, container, false)
        binding = fragmentBinding
        return fragmentBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        if (userViewModel.mAuthLiveData.value?.currentUser == null) {
            findNavController().navigate(R.id.action_global_authorizationFragment)
        } else {
            detectFirstInput()
            restoreStateRadioButton()
        }
    }

    override fun onStart() {
        super.onStart()

        // Слушатели
        clearSavedTextFromTextEdit()
        radioButtonListener()
        createObserver()
        startObservers()
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as MainActivity).changeHeader(taskViewModel.getUserData())

        CoroutineScope(Dispatchers.Main).launch {
            delay(900)
            updateTaskList()
        }
    }

    override fun onPause() {
        saveStateRadioButton()
        super.onPause()
    }

    override fun onStop() {
        removeObserver()
        super.onStop()
    }

    private fun createObserver() {
        /**Создает слушателей*/
        observerSelectorDay = Observer {
            if (taskViewModel.ldUserData.value?.id != null) {
                taskViewModel.getTask()
            }
        }

        observerSelectorState = Observer {
            if (taskViewModel.ldUserData.value?.id != null) {
                taskViewModel.getTask()
            }
        }

        observerArrayTask = Observer {
            createRecyclerView(it)
        }

    }

    private fun removeObserver() {
        /**Функция отключает слушателей*/
        taskViewModel.selectorDay.removeObserver(observerSelectorDay)
        taskViewModel.selectorState.removeObserver(observerSelectorState)
        taskViewModel.arrayTask.removeObserver(observerArrayTask)
    }

    private fun radioButtonListener() {
        /** Функция слушает переключения RadioButton и изменяет LiveData дня и состояния
         * заявки на выбранные */
        binding?.radioGroupData?.setOnCheckedChangeListener { _, id_rad_btn ->
            when (id_rad_btn) {
                R.id.r_btn_yesterday -> taskViewModel.setSelectorDay(YESTERDAY)
                R.id.r_btn_today -> taskViewModel.setSelectorDay(TODAY)
                R.id.r_btn_tomorrow -> taskViewModel.setSelectorDay(TOMORROW)
            }
        }

        binding?.radioGroupState?.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.r_btn_appointed -> taskViewModel.setSelectorState(TASK_APPOINTED)
                R.id.r_btn_closed -> taskViewModel.setSelectorState(TASK_CLOSED)
            }
        }
    }

    private fun startObservers() {
        /**Функция подключает слушатели к переключателю даты заявки и (назначена/закрыта)
         * и производит запрос данных если эти переключали менются*/

        // Слушатель состояния переключателей даты заявок
        taskViewModel.selectorDay.observe(viewLifecycleOwner, observerSelectorDay)

        // Слушатель состояния переключателей состояния заявки
        taskViewModel.selectorState.observe(viewLifecycleOwner, observerSelectorState)

        // Показываем RecyclerView как только получили заявки с сервера
        taskViewModel.arrayTask.observe(viewLifecycleOwner, observerArrayTask)

    }

    private fun getUserDataFromSharedPref() {
        /**Получаем данные польззователя из SharedPreference*/
        sharedPreferences = requireActivity().getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE)  // Получаем настройки сохраненые в Authorization
        val userData = sharedPreferences.getString(KEY_USER_DATA, null)
        if (userData != null) {
            println("Данные в таск фрагменте: $userData")
            taskViewModel.ldUserData.value = Json.decodeFromString(User.serializer(), userData)
        } else Toast.makeText(
            requireContext(),
            "Ошибка чтения данных пользователя",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun saveStateRadioButton() {
        /**Сохраняем положение RadioButton перед onPause*/
        taskViewModel.radioButtonDay.value = binding?.radioGroupData?.checkedRadioButtonId
        taskViewModel.radioButtonState.value = binding?.radioGroupState?.checkedRadioButtonId
    }

    private fun restoreStateRadioButton() {
        /**Восстанавливаем положение RadioButton в onResume*/

        if (taskViewModel.radioButtonDay.value != null) {
            binding?.radioGroupData?.check(taskViewModel.radioButtonDay.value!!)
        } else {
            binding?.radioGroupData?.check(R.id.r_btn_today)
        }

        if (taskViewModel.radioButtonState.value != null) {
            binding?.radioGroupState?.check(taskViewModel.radioButtonState.value!!)
        } else {
            binding?.radioGroupState?.check(R.id.r_btn_appointed)
        }
    }

    private fun detectFirstInput() {
        //  Если первое включение то выставляем заначения по умолчанию
        taskViewModel.setSelectorState(taskViewModel.getSelectorState())
        taskViewModel.setSelectorDay(taskViewModel.getSelectorDay())

        //  Если первый вход и нет данных пользователя в liveData получаем их из хранилища
        if (taskViewModel.ldUserData.value == null) {
            getUserDataFromSharedPref()
          //  mainActivity.changeHeader(taskViewModel.getUserData())  //
        } else {
            if (savedStateHandle.get<Boolean>(FIRST) == true) {
                getUserDataFromSharedPref()

           //     mainActivity.changeHeader(taskViewModel.getUserData())  //
                taskViewModel.getTaskDef()  // Обновляем список зявок
                savedStateHandle.set<Boolean>(FIRST, false)  // Убираем индикатор первой авторизации
            } else {
                taskViewModel.getTaskDef()  // Обновляем список зявок
            }
        }
    }

    private fun updateTaskList() {
        /**Функция обновляет список заявок*/
        if (taskViewModel.getSelectorState() == TASK_APPOINTED &&
            taskViewModel.getSelectorDay() == taskViewModel.getDateTime(TODAY)) {
            taskViewModel.getTaskDef()  // Если значени я по умолч. то обнов. с пармет. по умолч.
        } else {
            taskViewModel.getTask()  // Иначе обновляем с выставленными точками.
        }
    }

    private fun createRecyclerView(dataSet: ArrayList<TaskList>) {
        /**RecyclerView для назначеных заявок*/
        viewAdapter = MainAdapter(dataSet, findNavController(), taskViewModel)
        val viewManager = LinearLayoutManager(requireContext())
        val colorDrawableBackground = ColorDrawable(Color.parseColor("#ff0000"))
        val deleteIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_compited_task)!!

        binding?.recyclerView?.apply {
            setHasFixedSize(true)
            adapter = viewAdapter
            layoutManager = viewManager
        }

        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {  // or ItemTouchHelper.RIGHT
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, viewHolder2: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDirection: Int) {
                (viewAdapter as MainAdapter).removeItem(viewHolder.adapterPosition)
            }

            override fun isItemViewSwipeEnabled(): Boolean {
                /**Данная функция переопределяет доступность смахивания!!*/
                return taskViewModel.selectorState.value == TASK_APPOINTED
            }

            override fun onChildDraw (
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                val itemView = viewHolder.itemView
                val iconMarginVertical = (viewHolder.itemView.height - deleteIcon.intrinsicHeight) / 2

                if (dX > 0) {
                    colorDrawableBackground.setBounds(itemView.left, itemView.top, dX.toInt(), itemView.bottom)
                    deleteIcon.setBounds(itemView.left + iconMarginVertical, itemView.top + iconMarginVertical,
                        itemView.left + iconMarginVertical + deleteIcon.intrinsicWidth, itemView.bottom - iconMarginVertical)
                } else {
                    colorDrawableBackground.setBounds(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
                    deleteIcon.setBounds(itemView.right - iconMarginVertical - deleteIcon.intrinsicWidth, itemView.top + iconMarginVertical,
                        itemView.right - iconMarginVertical, itemView.bottom - iconMarginVertical)
                    deleteIcon.level = 0
                }

                colorDrawableBackground.draw(c)

                c.save()

                if (dX > 0)
                    c.clipRect(itemView.left, itemView.top, dX.toInt(), itemView.bottom)
                else
                    c.clipRect(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)

                deleteIcon.draw(c)
                c.restore()
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        }
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding?.recyclerView)

    }

    private fun clearSavedTextFromTextEdit() {
        /**Отчищает сохраненые в liveData значения полей TextEdit, коментарии и смму*/
        taskViewModel.savedComment = null
        taskViewModel.savedSumm = null
        taskViewModel.selectMaterial.value = arrayListOf()
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    companion object {
        // Индикатор первого включения
        const val FIRST = "FIRST"
    }
}