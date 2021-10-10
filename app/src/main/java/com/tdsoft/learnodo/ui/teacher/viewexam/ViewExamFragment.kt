package com.tdsoft.learnodo.ui.teacher.viewexam

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.tdsoft.learnodo.R
import com.tdsoft.learnodo.base.BaseFragment
import com.tdsoft.learnodo.common.hide
import com.tdsoft.learnodo.common.show
import com.tdsoft.learnodo.data.models.Exam
import kotlinx.android.synthetic.main.fragment_view_exam.*


class ViewExamFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_exam, container, false)
    }


    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listStudentMarks?.apply {
            layoutManager = LinearLayoutManager(requireContext())
        }
        val exam: Exam = arguments?.get("selectedExam") as Exam

        progressBar.show()
        learnodoRepo.getAllMarks(exam.examId!!) { it ->
            progressBar?.hide()
            if (!it.isNullOrEmpty()) {
                val adapter = AdapterMarks(it)
                listStudentMarks?.adapter = adapter

                val lawMarks = it.filter {
                    it.marks!! < 45
                }
                val averageMarks = it.filter {
                    it.marks!! in 45..74
                }

                val highMarks = it.filter {
                    it.marks!! in 75..100
                }
                txtLawStudentCount.text = "Law - ${lawMarks.size}"
                txtAvgStudentCount.text = "Avg - ${averageMarks.size}"
                txtHighStudentCount.text = "High - ${highMarks.size}"
                scoreGraphView.setMarks(lawMarks.size, averageMarks.size, highMarks.size)
            }else{
                showOKAlert("Not Found", "No result found") { dialogInterface: DialogInterface, i: Int ->
                    dialogInterface.dismiss()
                }
            }

        }
    }
}