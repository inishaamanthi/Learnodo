package com.tdsoft.learnodo.ui.teacher.exams

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.tdsoft.learnodo.R
import com.tdsoft.learnodo.base.BaseFragment
import com.tdsoft.learnodo.common.hide
import com.tdsoft.learnodo.common.show
import com.tdsoft.learnodo.data.models.Exam
import kotlinx.android.synthetic.main.fragment_student_exams.*


class TeacherExamsFragment : BaseFragment(), AdapterTeacherExams.OnTeacherExamClickListener {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_student_exams, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listStudentMarks.apply {
            layoutManager = LinearLayoutManager(context)
        }

        progressBar.show()
        learnodoRepo.getMyExams(){
            progressBar.hide()
            if(!it.isNullOrEmpty()){
                val adapter = AdapterTeacherExams(it)
                adapter.onTeacherExamClickListener = this
                listStudentMarks.adapter = adapter
            }
        }
    }

    override fun onStudentExamClicked(exam: Exam) {
        findNavController().navigate(R.id.viewExamFragment, bundleOf("selectedExam" to exam))
    }

}