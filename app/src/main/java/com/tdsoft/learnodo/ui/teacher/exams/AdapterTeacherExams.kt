package com.tdsoft.learnodo.ui.teacher.exams

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tdsoft.learnodo.R
import com.tdsoft.learnodo.common.inflate
import com.tdsoft.learnodo.data.models.Exam
import kotlinx.android.synthetic.main.listitem_exam.view.*

class AdapterTeacherExams(val list: List<Exam>) :
    RecyclerView.Adapter<AdapterTeacherExams.MarksViewHolder>() {

    interface OnTeacherExamClickListener{
        fun onStudentExamClicked(exam:Exam)
    }

    var onTeacherExamClickListener:OnTeacherExamClickListener? = null

    inner class MarksViewHolder(parent: ViewGroup) :
        RecyclerView.ViewHolder(parent.inflate(R.layout.listitem_exam)) {
        fun bind(item: Exam) = with(itemView) {
            txtExamTitle.text = item.examTitle.toString()
            itemView.setOnClickListener {
                onTeacherExamClickListener?.onStudentExamClicked(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = MarksViewHolder(parent)

    override fun onBindViewHolder(holder: MarksViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount() = list.size

}