package com.tdsoft.learnodo.ui.teacher.viewexam

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tdsoft.learnodo.R
import com.tdsoft.learnodo.common.inflate
import com.tdsoft.learnodo.data.models.Marks
import kotlinx.android.synthetic.main.listitem_marks.view.*

class AdapterMarks(val list: List<Marks>) : RecyclerView.Adapter<AdapterMarks.MarksViewHolder>() {

    inner class MarksViewHolder(parent: ViewGroup):RecyclerView.ViewHolder(parent.inflate(R.layout.listitem_marks)){
        fun bind(item:Marks) = with(itemView){
            txtStudentName.text = item.studentName
            txtMarks.text = item.marks.toString()
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = MarksViewHolder(parent)

    override fun onBindViewHolder(holder: MarksViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount() = list.size

}