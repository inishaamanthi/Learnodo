package com.tdsoft.learnodo.ui.student.exmas

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.tdsoft.learnodo.R
import com.tdsoft.learnodo.base.BaseBottomSheetDialogFragment
import com.tdsoft.learnodo.common.getDate
import com.tdsoft.learnodo.common.hide
import com.tdsoft.learnodo.common.show
import com.tdsoft.learnodo.data.models.Exam
import kotlinx.android.synthetic.main.layout_view_marks_bottom_sheet.*

class ViewMarksModalBottomSheet : BaseBottomSheetDialogFragment() {

    private lateinit var exam: Exam

    companion object {
        fun show(
            exam: Exam,
            fragmentManager: FragmentManager,
        ) {
            val bSheet = ViewMarksModalBottomSheet()
            bSheet.exam = exam
            bSheet.show(fragmentManager, "ViewMarksModalBottomSheet")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.layout_view_marks_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressBar?.show()
        learnodoRepo.getMarks(exam.examId!!, learnodoRepo.getCurrentUser()!!.userId!!) {
            progressBar?.hide()
            it?.let {
                txtExamTitle?.text = it.examTitle.plus(" Result")
                txtExamDate?.text = exam.examDateTimeStamp?.getDate()
                txtMarks?.text = it.marks.toString()
            }
            if (it == null) {
                showOKAlert(
                    "No result fount", " Result not found try again later"
                ) { dialog, _ ->
                    dialog.dismiss()
                    this@ViewMarksModalBottomSheet.dismiss()
                }
            }
        }


    }
}