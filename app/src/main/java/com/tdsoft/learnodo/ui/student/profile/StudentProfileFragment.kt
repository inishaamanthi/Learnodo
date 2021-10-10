package com.tdsoft.learnodo.ui.student.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tdsoft.learnodo.R
import com.tdsoft.learnodo.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_student_profile.*


class StudentProfileFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_student_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        txtName.text = learnodoRepo.getCurrentUser()!!.name
    }
}