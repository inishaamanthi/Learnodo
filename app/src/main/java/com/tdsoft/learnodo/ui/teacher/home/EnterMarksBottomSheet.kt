package com.tdsoft.learnodo.ui.teacher.home

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.FragmentManager
import com.tdsoft.learnodo.R
import com.tdsoft.learnodo.base.BaseBottomSheetDialogFragment
import com.tdsoft.learnodo.common.hide
import com.tdsoft.learnodo.common.show
import com.tdsoft.learnodo.data.models.Exam
import com.tdsoft.learnodo.data.models.Marks
import com.tdsoft.learnodo.data.models.UserInformation
import kotlinx.android.synthetic.main.layout_enter_marks.*

class EnterMarksBottomSheet : BaseBottomSheetDialogFragment() {

    private lateinit var student: UserInformation

    companion object {
        fun show(
            student: UserInformation,
            fragmentManager: FragmentManager,
        ) {
            val bSheet = EnterMarksBottomSheet()
            bSheet.student = student
            bSheet.show(fragmentManager, "EnterMarksBottomSheet")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.layout_enter_marks, container, false)
    }

    private var selectedExam: Exam? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnCancel.setOnClickListener {
            dismiss()
        }
        txtStudentName.text = "Enter marks for ".plus(student.name)
        progressBar?.show()
        learnodoRepo.getMyExams {
            progressBar?.hide()
            if (it.isNullOrEmpty()) {
                showOKAlert(
                    "No found",
                    "No exams found, Try later"
                ) { dialogInterface: DialogInterface, _: Int ->
                    dialogInterface.dismiss()
                    this@EnterMarksBottomSheet.dismiss()
                }
                return@getMyExams
            }

            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, it)
            val item = (textInputExams.editText as? AutoCompleteTextView)
            item!!.setOnItemClickListener { adapterView, view, i, l ->
                selectedExam = it[i]
            }
            item.setAdapter(adapter)
        }

        btnSubmit.setOnClickListener {
            val marks = Marks()
            if (selectedExam == null) {
                textInputExams.error = "Please select exam"
                return@setOnClickListener
            }

            textInputExams.error = null

            if(textInputMarks.editText?.text.isNullOrEmpty()){
                textInputMarks.error = "Please enter marks"
            }
            textInputMarks.error = null
            marks.examId = selectedExam!!.examId
            marks.studentId = student.userId
            marks.examTitle = selectedExam!!.examTitle
            marks.studentName = student.name
            marks.marks = textInputMarks.editText!!.text.toString().toInt()
            learnodoRepo.enterMarks(marks){
                if(!it){
                    showOKAlert(
                        "Failed",
                        "Marks not submitted, something went wrong"
                    ) { dialogInterface: DialogInterface, _: Int ->
                        dialogInterface.dismiss()
                    }
                }else{
                    showOKAlert(
                        "Success",
                        "Marks submitted successfully"
                    ) { dialogInterface: DialogInterface, _: Int ->
                        dialogInterface.dismiss()
                        this@EnterMarksBottomSheet.dismiss()
                    }
                }
            }
        }
    }
}