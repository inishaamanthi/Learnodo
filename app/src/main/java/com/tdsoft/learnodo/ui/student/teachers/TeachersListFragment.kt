package com.tdsoft.learnodo.ui.student.teachers

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.tdsoft.learnodo.R
import com.tdsoft.learnodo.base.BaseFragment
import com.tdsoft.learnodo.common.CLASS_ID
import com.tdsoft.learnodo.common.sendEmail
import com.tdsoft.learnodo.data.models.UserInformation
import kotlinx.android.synthetic.main.fragment_teachers_list.*


class TeachersListFragment : BaseFragment(), AdapterTeachers.OnSendEmailClickListener {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_teachers_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadClassDetails()
    }

    private fun loadClassDetails() {
        learnodoRepo.getClassRoom(CLASS_ID) {
            if (it != null) {
                val adapterMaterials =
                    AdapterTeachers(it.teachers?.values?.toList() ?: emptyList())
                adapterMaterials.onSendEmailClickListener = this@TeachersListFragment
                with(listTeachers) {
                    layoutManager = LinearLayoutManager(context)
                    adapter = adapterMaterials
                }
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

    override fun onSendEmailClicked(userInformation: UserInformation) {
        userInformation.email?.sendEmail(requireContext())
    }
}