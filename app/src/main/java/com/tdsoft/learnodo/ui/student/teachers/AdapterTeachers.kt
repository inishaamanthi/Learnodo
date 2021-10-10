package com.tdsoft.learnodo.ui.student.teachers

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tdsoft.learnodo.R
import com.tdsoft.learnodo.common.inflate
import com.tdsoft.learnodo.data.models.UserInformation
import kotlinx.android.synthetic.main.listitem_teacher.view.*

class AdapterTeachers(private val list: List<UserInformation>) :
    RecyclerView.Adapter<AdapterTeachers.TeacherViewHolder>() {

    var onSendEmailClickListener:OnSendEmailClickListener? = null

    interface OnSendEmailClickListener{
        fun onSendEmailClicked(userInformation: UserInformation)
    }

    inner class TeacherViewHolder(parent: ViewGroup) :
        RecyclerView.ViewHolder(parent.inflate(R.layout.listitem_teacher)) {
        fun bind(item: UserInformation) = with(itemView) {
            txtTeacherName.text = item.name
            txtTeacherEmail.text = item.email
            txtTeacherPhone.text = item.phoneNumber
            Glide.with(this).load(item.profileImage)
                .error(R.drawable.ic_baseline_person_outline_24).into(imgTeacherProfile);

            btnSendEmail.setOnClickListener {
                onSendEmailClickListener?.onSendEmailClicked(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = TeacherViewHolder(parent)

    override fun onBindViewHolder(holder: TeacherViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount() = list.size

}