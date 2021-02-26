package android.bignerdranch.photounit.fragments.task

import android.bignerdranch.photounit.R
import android.bignerdranch.photounit.activity.MainActivity
import android.bignerdranch.photounit.databinding.FragmentTaskBinding
import android.bignerdranch.photounit.fragments.BaseFragment
import android.bignerdranch.photounit.model.modelsDB.TaskModel
import android.bignerdranch.photounit.model.otherModel.User
import android.bignerdranch.photounit.utilits.*
import android.bignerdranch.photounit.utilits.recyclerView.MainAdapter
import android.bignerdranch.photounit.utilits.recyclerView.OnItemLongClickListener
import android.bignerdranch.photounit.viewModels.TaskViewModel
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json


class TaskFragment : BaseFragment(R.layout.fragment_task), OnItemLongClickListener {

    private val taskViewModel: TaskViewModel by activityViewModels()
    private var binding: FragmentTaskBinding? = null

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var savedStateHandle: SavedStateHandle

    private lateinit var viewAdapter: RecyclerView.Adapter<*>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val navController = findNavController()
        val currentBackStackEntry = navController.currentBackStackEntry!!
        savedStateHandle = currentBackStackEntry.savedStateHandle
    }


    @ExperimentalSerializationApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (FirebaseAuth.getInstance().currentUser == null) {
            findNavController().navigate(R.id.action_global_authorizationFragment)
        }
        else {
            val fragmentTaskBinding = FragmentTaskBinding.bind(view)
            binding = fragmentTaskBinding

            detectFirstInput()
            restoreStateRadioButton()
            startObserverArrayTask()
            showSnackBar(view)
        }
    }

    @ExperimentalSerializationApi
    override fun onStart() {
        super.onStart()

        // Слушатели
        clearSavedTextFromTextEdit()
        radioButtonListener()
    }

    @ExperimentalSerializationApi
    override fun onResume() {
        super.onResume()

        updateTask()
        (requireActivity() as MainActivity).changeHeader(taskViewModel.getUserData())
    }

    override fun onPause() {
        saveStateRadioButton()
        super.onPause()
    }

    @ExperimentalSerializationApi
    private fun updateTask(_showProgress: Boolean = false) {
        var showProgress = _showProgress
        val bundle: Bundle = this.requireArguments()
        if (!bundle.isEmpty) {
            val isFromCloseTask: Boolean = bundle.getBoolean(KEY_IS_CLOSE_FRAGMENT)
            showProgress = !isFromCloseTask

        }
        coroutineScope.launch(exceptionHandler) {
            if (showProgress) visibleProgressBar()
            taskViewModel.updateTask()
            invisibleProgressBar()
        }
    }

    private suspend fun visibleProgressBar() = withContext(Dispatchers.Main) {
        binding?.taskLoadProgress?.isVisible = true
        binding?.taskRecyclerView?.adapter = null
    }

    private suspend fun invisibleProgressBar() = withContext(Dispatchers.Main) {
        binding?.taskLoadProgress?.isGone = true
    }

    @ExperimentalSerializationApi
    private fun showSnackBar(view: View) {
        val bundle: Bundle = this.requireArguments()
        if (!bundle.isEmpty) {
            val message: String = bundle.getString(KEY_RESULT_CLOSE) ?: "Ошибка TaskFragment.."
            val snackBar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
            snackBar.show()
        }
    }

    @ExperimentalSerializationApi
    private fun startObserverArrayTask() {
        /**Показываем RecyclerView как только получили заявки с сервера*/
        taskViewModel.arrayTask.observe(viewLifecycleOwner) {
            createRecyclerView(it)
        }
    }


    @ExperimentalSerializationApi
    private fun radioButtonListener() {
        /** Функция слушает переключения RadioButton и изменяет LiveData дня и состояния
         * заявки на выбранные */
        binding?.radioGroupData?.setOnCheckedChangeListener { _, id_rad_btn ->
            when (id_rad_btn) {
                R.id.r_btn_yesterday -> {
                    taskViewModel.setSelectorDay(YESTERDAY)
                    updateTask(true)
                }
                R.id.r_btn_today -> {
                    taskViewModel.setSelectorDay(TODAY)
                    updateTask(true)
                }
                R.id.r_btn_tomorrow -> {
                    taskViewModel.setSelectorDay(TOMORROW)
                    updateTask(true)
                }
            }
        }

        binding?.radioGroupState?.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.r_btn_appointed -> {
                    taskViewModel.setSelectorState(TASK_APPOINTED)
                    updateTask(true)
                }
                R.id.r_btn_closed -> {
                    taskViewModel.setSelectorState(TASK_CLOSED)
                    updateTask(true)
                }
            }
        }
    }

    private fun getUserDataFromSharedPref() {
        /**Получаем данные польззователя из SharedPreference*/

        sharedPreferences = requireActivity().getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE)  // Получаем настройки сохраненые в Authorization
        val userData = sharedPreferences.getString(KEY_USER_DATA, null)
        if (userData != null) {
            taskViewModel.ldUserData.value = Json.decodeFromString(User.serializer(), userData)
        } else Toast.makeText(
            requireContext(),
            "Ошибка чтения данных пользователя",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun saveStateRadioButton() {
        /**Сохраняем положение RadioButton перед onPause*/

        taskViewModel.apply {
            radioButtonDay.value = binding?.radioGroupData?.checkedRadioButtonId
            radioButtonState.value = binding?.radioGroupState?.checkedRadioButtonId
        }
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


    @ExperimentalSerializationApi
    private fun detectFirstInput() {
        //  Если первое включение то выставляем заначения по умолчанию
        taskViewModel.apply {
            setSelectorState(taskViewModel.getSelectorState())
            setSelectorDay(taskViewModel.getSelectorDay())
        }

        //  Если первый вход и нет данных пользователя в liveData получаем их из хранилища
        if (taskViewModel.ldUserData.value == null) {
            getUserDataFromSharedPref()

        } else {
            if (savedStateHandle.get<Boolean>(FIRST) == true) {
                getUserDataFromSharedPref()
                updateTask(true)  // Обновляем список зявок
                savedStateHandle.set<Boolean>(FIRST, false)  // Убираем индикатор первой авторизации
            }
        }
    }

    override fun onItemLongClicked(task: TaskModel, address: String) {
        /**Слушатель динных нажатий RecyclerView*/

        //  Передаем данные в ClientCardFragment
        val bundle = bundleOf(
            KEY_ID_CLIENT to task.id_client,
            KEY_ADDRESS_CLIENT to address,
            KEY_PHONE_CLIENT to task.phones
        )
        findNavController().navigate(R.id.action_taskFragment_to_clientCardFragment, bundle)
    }

    private fun createRecyclerView(dataSet: ArrayList<TaskModel>) {
        /**RecyclerView для назначеных заявок*/
        viewAdapter = MainAdapter(dataSet,this , findNavController(), taskViewModel)
        val viewManager = LinearLayoutManager(requireContext())
        val colorDrawableBackground = ColorDrawable(Color.parseColor("#718792"))
        val deleteIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_compited_task)!!

        binding?.taskRecyclerView?.apply {
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
        itemTouchHelper.attachToRecyclerView(binding?.taskRecyclerView)

    }

    private fun clearSavedTextFromTextEdit() {
        /**Отчищает сохраненые в liveData значения полей TextEdit, коментарии и смму*/
        taskViewModel.apply {
            savedComment = null
            savedSumm = null
            selectMaterial.value = arrayListOf()
        }
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    companion object {
        // Индикатор первого включения
        const val FIRST = "FIRST"
        private const val KEY_RESULT_CLOSE = "result_close"
        private const val KEY_IS_CLOSE_FRAGMENT = "from_close_fragment"
        const val KEY_ID_CLIENT = "id_client"
        const val KEY_ADDRESS_CLIENT = "address_client"
        const val KEY_PHONE_CLIENT = "phone_client"
    }


}