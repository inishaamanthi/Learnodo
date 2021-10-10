package com.tdsoft.learnodo.ui.teacher.home

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.tdsoft.learnodo.R
import com.tdsoft.learnodo.base.BaseFragment
import com.tdsoft.learnodo.common.CLASS_ID
import com.tdsoft.learnodo.common.hide
import com.tdsoft.learnodo.common.sendEmail
import com.tdsoft.learnodo.common.show
import com.tdsoft.learnodo.data.models.UserInformation
import kotlinx.android.synthetic.main.fragment_teacher_home.*

class TeacherHomeFragment : BaseFragment(), AdapterStudents.OnSendEmailForStudentClickListener,
    AdapterStudents.OnStudentClickListener {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_teacher_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(listStudents) {
            layoutManager = LinearLayoutManager(requireContext())
        }
        loadStudents()
    }
    private fun loadStudents(){
        progressBar?.show()
        learnodoRepo.getStudents(CLASS_ID) {
            progressBar.hide()
            if (it != null) {
                val newList = it.filterNotNull()
                val adapterStudents = AdapterStudents(newList)
                adapterStudents.onSendEmailForStudentClickListener = this@TeacherHomeFragment
                adapterStudents.onStudentClickListener = this@TeacherHomeFragment
                listStudents?.adapter = adapterStudents
            } else {
                showOKAlert(
                    "No class found",
                    "You have not registered with any class"
                ) { dialogInterface: DialogInterface, i: Int ->
                    dialogInterface.dismiss()
                }
            }
        }
    }

    override fun onSendEmailForStudentClicked(userInformation: UserInformation) {
        userInformation.email?.sendEmail(requireContext())
    }

    override fun onStudentClicked(userInformation: UserInformation) {
        EnterMarksBottomSheet.show(userInformation, childFragmentManager)
    }
}