package android.bignerdranch.photounit.fragments

import android.bignerdranch.photounit.R
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
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_task.*
import kotlinx.serialization.json.Json


class TaskFragment : BaseFragment(R.layout.fragment_task) {

    private val taskViewModel: TaskViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var savedStateHandle: SavedStateHandle

    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var colorDrawableBackground: ColorDrawable
    private lateinit var deleteIcon: Drawable


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val navController = findNavController()
        val currentBackStackEntry = navController.currentBackStackEntry!!
        savedStateHandle = currentBackStackEntry.savedStateHandle

        savedStateHandle.getLiveData<Boolean>(AuthorizationFragment.LOGIN_SUCCESSFUL)
            .observe(currentBackStackEntry, Observer { success ->
                if (!success) {
                    val startDestination = navController.graph.startDestination
                    val navOptions = NavOptions.Builder()
                        .setPopUpTo(startDestination, true)
                        .build()
                    navController.navigate(startDestination, null, navOptions)
                }
            })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userViewModel.mAuthLiveData.observe(viewLifecycleOwner, {
            if (it.currentUser == null) {
                navController.navigate(R.id.action_global_authorizationFragment)
            } else {
                detectFirstInput()
                restoreStateRadioButton()

                // Слушатели
                radioButtonListener()
                getRequestIfChangePositionRadioButton()
                createListenerReceivedTask()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        mainActivity.changeHeader(taskViewModel.getUserData())
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
                R.id.r_btn_yesterday -> taskViewModel.setSelectorDay(YESTERDAY)
                R.id.r_btn_today -> taskViewModel.setSelectorDay(TODAY)
                R.id.r_btn_tomorrow -> taskViewModel.setSelectorDay(TOMORROW)
            }
        }

        radioGroupState.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.r_btn_appointed -> taskViewModel.setSelectorState(TASK_APPOINTED)
                R.id.r_btn_closed -> taskViewModel.setSelectorState(TASK_CLOSED)
            }
        }
    }

    private fun getRequestIfChangePositionRadioButton() {
        /**Функция подключает слушатели к переключателю даты заявки и (назначена/закрыта)
         * и производит запрос данных если эти переключали менются*/

        // Слушатель состояния переключателей даты заявок
        taskViewModel.selectorDay.observe(viewLifecycleOwner, {
            if (taskViewModel.ldUserData.value?.id != null) {
                taskViewModel.getTask()
            }
        })

        // Слушатель состояния переключателей состояния заявки
        taskViewModel.selectorState.observe(viewLifecycleOwner, {
            if (taskViewModel.ldUserData.value?.id != null) {
                taskViewModel.getTask()
            }
        })
    }

    private fun createListenerReceivedTask() {
        // Показываем RecyclerView как только получили заявки с сервера
        taskViewModel.mapTask.observe(viewLifecycleOwner, { arrayTask ->
            createRecyclerView(arrayTask)
        })
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
        //  Если первое включение то выставляем заначения по умолчанию
        if ( taskViewModel.selectorState.value == null) taskViewModel.setSelectorState(TASK_APPOINTED)
        if (taskViewModel.selectorDay.value == null)  taskViewModel.setSelectorDay(TODAY)

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

    private fun createRecyclerView(dataSet: ArrayList<TaskList>) {
        /**RecyclerView для назначеных заявок*/

        viewAdapter = MainAdapter(dataSet, navController, taskViewModel)
        viewManager = LinearLayoutManager(requireContext())
        colorDrawableBackground = ColorDrawable(Color.parseColor("#ff0000"))
        deleteIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_compited_task)!!

        recyclerView.apply {
            setHasFixedSize(true)
            adapter = viewAdapter
            layoutManager = viewManager
            //addItemDecoration(DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL))
        }

        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, viewHolder2: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDirection: Int) {
                (viewAdapter as MainAdapter).removeItem(viewHolder.adapterPosition, viewHolder)
            }

            override fun isItemViewSwipeEnabled(): Boolean {
                /**Данная функция переопределяет доступность смахивания!!*/
                return taskViewModel.selectorState.value == TASK_APPOINTED
            }

            override fun onChildDraw(
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
        itemTouchHelper.attachToRecyclerView(recyclerView)

    }

    companion object {
        // Индикатор первого включения
        const val FIRST = "FIRST"
    }
}