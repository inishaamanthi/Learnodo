package com.tdsoft.learnodo.ui.student.exmas

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tdsoft.learnodo.R
import com.tdsoft.learnodo.common.inflate
import com.tdsoft.learnodo.data.models.Exam
import kotlinx.android.synthetic.main.listitem_exam.view.*

class AdapterStudentExams(val list: List<Exam>) :
    RecyclerView.Adapter<AdapterStudentExams.MarksViewHolder>() {

    interface OnStudentExamListener{
        fun onStudentExamClicked(exam:Exam)
    }

    var onStudentExamListener:OnStudentExamListener? = null

    inner class MarksViewHolder(parent: ViewGroup) :
        RecyclerView.ViewHolder(parent.inflate(R.layout.listitem_exam)) {
        fun bind(item: Exam) = with(itemView) {
            txtExamTitle.text = item.examTitle.toString()
            itemView.setOnClickListener {
                onStudentExamListener?.onStudentExamClicked(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = MarksViewHolder(parent)

    override fun onBindViewHolder(holder: MarksViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount() = list.size

}