package android.bignerdranch.mobilecrm.utilits.helpers

import android.bignerdranch.mobilecrm.R
import android.bignerdranch.mobilecrm.model.modelsDB.ClientCardBilling
import android.bignerdranch.mobilecrm.model.modelsDB.ClientsEntrance
import android.bignerdranch.mobilecrm.model.networkModel.ResultLinkStatus
import android.bignerdranch.mobilecrm.ui.fragments.task.ClientCardFragment
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import smartadapter.extension.SmartViewHolderBinder
import smartadapter.viewevent.model.ViewEvent
import smartadapter.viewevent.swipe.BasicSwipeEventBinder
import smartadapter.viewevent.swipe.SwipeFlags


fun detectUserStatus(status: String): String =
    when (status) {
        "adm" -> "Администратор"
        "mnt" -> "Мастер"
        "disp" -> "Диспетчер"
        else -> "Пользователь"
    }

fun AppCompatActivity.detectUserIcon(status: String): Drawable? =
    when (status) {
        "adm" -> ContextCompat.getDrawable(this, R.drawable.ic_admin_2)
        "mnt" -> ContextCompat.getDrawable(this, R.drawable.ic_master)
        "disp" -> ContextCompat.getDrawable(this, R.drawable.ic_operator)
        else -> ContextCompat.getDrawable(this, R.drawable.ic_master)

    }

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

fun View.showKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
}

fun detectProblem(pool1: String, pool2: String, pool3: String, service: String): String {
    /** Функция формирует дополнительное описание заявок. Поле "Неполадки" */
    return when (service) {
        "Интернет" -> forPoolInternet(pool1, pool2, pool3)
        "Телевидение" -> forPoolTelevid(pool1, pool2, pool3)
        "Домофон" -> forPoolDomofon(pool1, pool2)
        else -> ""
    }

}

fun forPoolDomofon(pool1: String, pool2: String): String {
    var param1 = ""
    var param2 = ""
    val data1 = pool1.splitToSequence("*")
    val data2 = pool2.splitToSequence("*")

    data1.forEachIndexed { index, it ->
        if (it == "1") {
            param1 = when (index) {
                0 -> "Новое подключение. "
                1 -> "Подключение. "
                2 -> "Отключение. "
                else -> ""
            }
        }
    }

    data2.forEachIndexed { index, it ->
        if (it == "1") {
            param2 = when (index) {
                0 -> "До квартиры не доходит сигнал"
                1 -> "Доводчик"
                2 -> "Магнит"
                3 -> "Параллельный сигнал"
                4 -> "Трубка (УКП)"
                5 -> "Панель"
                6 -> "Не работает домофон"
                7 -> "Ключ"
                8 -> "Поменять код"
                else -> ""
            }
        }
    }
    return param1 + param2
}

fun forPoolTelevid(pool1: String, pool2: String, pool3: String): String {
    var param1 = ""
    var param2 = ""
    var param3 = ""
    val data1 = pool1.splitToSequence("*")
    val data2 = pool2.splitToSequence("*")
    val data3 = pool3.splitToSequence("*")
    data1.forEachIndexed { index, it ->
        if (it == "1") {
            param1 = when (index) {
                0 -> "Новое подключение. "
                1 -> "Переподключение. "
                2 -> "Повторное подключение. "
                3 -> "Подключение доп.точки. "
                4 -> "Отключение по заявлению. "
                5 -> "Отключение за неуплату. "
                6 -> "Приостановление. "
                else -> ""
            }
        }
    }
    data2.forEachIndexed { index, it ->
        if (it == "1") {
            param2 = when (index) {
                0 -> "Не показ. аналоговые каналы. "
                1 -> "Не показ. цифровые каналы. "
                2 -> "Помехи. "
                3 -> "Нет сигнала. "
                4 -> "Оборван кабель. "
                5 -> "Замена кабеля в квартире. "
                6 -> "Замена кабеля в подъезде. "
                else -> ""
            }
        }
    }
    data3.forEachIndexed { index, it ->
        if (it == "1") {
            param3 = when (index) {
                1 -> "Настройка аналоговых каналов"
                2 -> "Настройка цифровых каналов"
                3 -> "Сортировка каналов"
                else -> ""
            }
        }
    }
    return param1 + param2 + param3
}

fun forPoolInternet(pool1: String, pool2: String, pool3: String): String {
    var param1 = ""
    var param2 = ""
    var param3 = ""
    val data1 = pool1.splitToSequence("*")
    val data2 = pool2.splitToSequence("*")
    val data3 = pool3.splitToSequence("*")
    data1.forEachIndexed { index, it ->
        if (it == "1") {
            param1 = when (index) {
                0 -> "Доподключение. "
                1 -> "Проверить скорость. "
                2 -> "Частые отключения. "
                3 -> "Возобновить. "
                else -> ""
            }
        }
    }
    data2.forEachIndexed { index, it ->
        if (it == "1") {
            param2 = when (index) {
                0 -> "Сетевой кабель не подключен. "
                1 -> "Настроить VPN. "
                2 -> "Настроить Роутер. "
                3 -> "Заменить кабель. "
                4 -> "Нарастить кабель. "
                5 -> "Переобжать коннектор. "
                else -> ""
            }
        }
    }
    data3.forEachIndexed { index, it ->
        if (it == "1") {
            param3 = when (index) {
                1 -> "Перезагрузить Свитч"
                2 -> "Перезагрузить ONU"
                else -> ""
            }
        }
    }
    return param1 + param2 + param3

}

fun getStateClients(client: ClientsEntrance): String {
    var state: String = ""
    when (client.status) {
        0 -> if (client.expired != null) state = "Отключен"
        1 -> if (client.expired == null) state = "Подключен"
        2 -> state = "В проц. подкл."
        3 -> state = "Откл. по заяв."
        4 -> if (client.tv) state = "Откл. за долг"
        5 -> state = "Подключен"
        6 -> state = "Откл. физич"
    }
    return state
}

fun detectService(map: Map<String, Boolean>): String {
    /** Проверяет на какой услуге в базе стоит true и возвращает ее имя */
    var i = ""
    map.forEach {
        if (it.value) {
            i = it.key
        }
    }
    return i
}

class SwipeRemoveItemBinder(
    override var swipeFlags: SwipeFlags,
    override var eventListener: (ViewEvent.OnItemSwiped) -> Unit,
) : BasicSwipeEventBinder(
    eventListener = eventListener
), SmartViewHolderBinder {

    override fun onChildDraw(
        canvas: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        val itemView = viewHolder.itemView
        val icon = ContextCompat.getDrawable(
            viewHolder.itemView.context,
            R.drawable.ic_delete_material
        )
        val background = ColorDrawable(Color.parseColor("#718792"))

        val iconMargin = (itemView.height - icon!!.intrinsicHeight) / 2
        val iconTop = itemView.top + (itemView.height - icon.intrinsicHeight) / 2
        val iconBottom = iconTop + icon.intrinsicHeight

        val iconLeft = itemView.right - iconMargin - icon.intrinsicWidth
        val iconRight = itemView.right - iconMargin

        icon.setBounds(
            iconLeft,
            iconTop,
            iconRight,
            iconBottom
        )

        background.setBounds(
            (itemView.right + dX).toInt(),
            itemView.top,
            itemView.right,
            itemView.bottom
        )

        if (dX.toInt() == 0) { // view is unSwiped
            background.setBounds(0, 0, 0, 0)
        }

        background.draw(canvas)

        if (-dX > (icon.intrinsicWidth + iconMargin)) // Draw icon only on full visibility
            icon.draw(canvas)
    }
}

fun Fragment.returnDateStart(card: ClientCardBilling): String {
    return if (card.dateStart != "") "\n${card.dateStart}" else ""
}

fun Fragment.returnMAC(card: ClientCardBilling): String {
    return if (card.mac != "") "\n${card.mac}" else ""
}

fun Fragment.translateState(state: String?): String {
    return when (state) {
        "4" -> "Недостаточно средств"
        "0" -> "Активна"
        "10" -> "Отключена"
        else -> "Ошибка"
    }
}

fun ClientCardFragment.changeIcon(link: ResultLinkStatus): Int {
    return when (link.state) {
        "Up" -> R.drawable.ic_ethernet_up1
        "Down" -> R.drawable.ic_ethernet_down1
        else -> R.drawable.ic_ethernet_netral1
    }
}




