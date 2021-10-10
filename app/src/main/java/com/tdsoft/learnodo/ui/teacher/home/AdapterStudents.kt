package com.tdsoft.learnodo.ui.teacher.home

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tdsoft.learnodo.R
import com.tdsoft.learnodo.common.inflate
import com.tdsoft.learnodo.data.models.UserInformation
import kotlinx.android.synthetic.main.listitem_student.view.*

class AdapterStudents(private val list: List<UserInformation>) :
    RecyclerView.Adapter<AdapterStudents.StudentViewHolder>() {
    var onSendEmailForStudentClickListener:OnSendEmailForStudentClickListener? = null
    var onStudentClickListener:OnStudentClickListener? = null

    interface OnSendEmailForStudentClickListener{
        fun onSendEmailForStudentClicked(userInformation: UserInformation)
    }

    interface OnStudentClickListener{
        fun onStudentClicked(userInformation: UserInformation)
    }
    inner class StudentViewHolder(parent: ViewGroup) :
        RecyclerView.ViewHolder(parent.inflate(R.layout.listitem_student)) {
        fun bind(item: UserInformation) = with(itemView) {
            txtStudentName.text = item.name
            txtStudentEmail.text = item.email
            txtStudentPhone.text = item.phoneNumber
            Glide.with(this).load(item.profileImage)
                .error(R.drawable.ic_baseline_person_outline_24).into(imgStudentProfile);
            btnSendEmailToStudent.setOnClickListener {
                onSendEmailForStudentClickListener?.onSendEmailForStudentClicked(item)
            }

            itemView.setOnClickListener {
                onStudentClickListener?.onStudentClicked(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = StudentViewHolder(parent)

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount() = list.size

}