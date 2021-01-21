package android.bignerdranch.photounit.viewModels

import android.bignerdranch.photounit.R
import android.bignerdranch.photounit.model.MaterialUsed
import android.bignerdranch.photounit.model.User
import android.bignerdranch.photounit.model.modelsDB.TaskList
import android.bignerdranch.photounit.utilits.DataBaseCommunication
import android.bignerdranch.photounit.utilits.viewHolder.SimpleExpandableItemViewHolder
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import smartadapter.viewevent.listener.OnMultiItemSelectListener
import smartadapter.viewevent.model.ViewEvent
import smartadapter.viewevent.viewmodel.ViewEventViewModel


class TaskViewModel : ViewModel(), DataBaseCommunication {

    val mapTask = MutableLiveData<ArrayList<TaskList>>()  // Массив заявок с сервера
    val singleTask = MutableLiveData<TaskList>()  // Данные одной заявки
    val selectorDay = MutableLiveData<String>()
    val selectorState = MutableLiveData<Char>()
    val resultCloseTask = MutableLiveData<String>()  // Содержит ответ сервера после закрытия заявки


    val selectMaterial = MutableLiveData<ArrayList<MaterialUsed>>()  // Здесь содержатся выбранные для списания материалы
    val arrayMaterialList = MutableLiveData<ArrayList<MaterialUsed>>()  // Сюда загружается список доступных материалов с сервера

    val textEditComment = MutableLiveData<String>()
    val textEditSumma = MutableLiveData<String>()

    val ldUserData = MutableLiveData<User>()  // Данные пользователя из Firebase

    val radioButtonDay = MutableLiveData<Int>()
    val radioButtonState = MutableLiveData<Int>()

    init {
        selectMaterial.value = arrayListOf()  // Инициализируем масив
    }
}

class MultiItemSelectViewModel : ViewEventViewModel<ViewEvent, OnMultiItemSelectListener>(
    OnMultiItemSelectListener(
        enableOnLongClick = false,
        viewHolderType = SimpleExpandableItemViewHolder::class,
        selectableItemType = Integer::class,
        viewId = R.id.closeTaskItem
    )
)