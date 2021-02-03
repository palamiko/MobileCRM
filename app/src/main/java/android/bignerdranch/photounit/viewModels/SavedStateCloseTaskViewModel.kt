package android.bignerdranch.photounit.viewModels

import android.text.Editable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

class SavedStateCloseTaskViewModel( private val savedState: SavedStateHandle): ViewModel() {

    private var saveComment: String
    private var saveSumm: String
    //val savedComment = savedState.get<String>(COMMENT)
    //val savedSumm = savedState.get<String>(SUMM)


    init {
        saveComment = savedState.get<String>(COMMENT).toString()
        saveSumm = savedState.get<String>(SUMM) ?: ""
    }

    fun getComment(): String {
        println("@@@@@@@@@@@@@@@@$saveComment")
        return saveComment
    }

    fun saveTextEdit(comment:Editable, summ:Editable) {
        println("SAVED!!!!!!!!!!  $comment")

        //if (comment != null) {
            //saveComment = comment.toString()
            savedState.set(COMMENT, comment.toString())
       // }
      //  if (summ != null) {
           // saveSumm = summ.toString()
            savedState.set(SUMM, summ.toString())
     //   }
    }




    companion object {
        private const val COMMENT = "text_edit_comment"
        private const val SUMM = "text_edit_summ"
    }
}

