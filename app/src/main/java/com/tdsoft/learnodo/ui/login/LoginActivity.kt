package com.tdsoft.learnodo.ui.login

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import com.tdsoft.learnodo.R
import com.tdsoft.learnodo.base.BaseActivity
import com.tdsoft.learnodo.common.UserTypes
import com.tdsoft.learnodo.common.hide
import com.tdsoft.learnodo.common.show
import com.tdsoft.learnodo.ui.signup.SignUpActivity
import com.tdsoft.learnodo.ui.student.StudentMainActivity
import com.tdsoft.learnodo.ui.teacher.TeacherMainActivity
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.btnLogin
import kotlinx.android.synthetic.main.activity_sign_up.*

class LoginActivity : BaseActivity() {
    companion object{
        fun startActivity(context: Context){
            val intent = Intent(context, LoginActivity::class.java)
            context.startActivity(intent)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initUI()
    }

    private fun initUI() {
        btnLogin.setOnClickListener {
            if(tlEmail.editText!!.text.isNullOrEmpty()){
                tlEmail.error = "Email cannot be empty"
                return@setOnClickListener
            }
            tlEmail.error = null

            if(tlPassword.editText!!.text.isNullOrEmpty()){
                tlPassword.error = "Password cannot be empty"
                return@setOnClickListener
            }

            tlPassword.error = null

            progressBar.show(btnLogin)
            learnodoRepo.login(tlEmail.editText!!.text.toString(), tlPassword.editText!!.text.toString()) {
                progressBar.hide(btnLogin)
                if (it.isSuccess) {
                    learnodoRepo.saveCurrentUser(it.userInformationObj!!)
                    showOKAlert(
                        "Success",
                        it.message,
                        onOkClick = { dialogInterface: DialogInterface, _: Int ->
                            dialogInterface.dismiss()
                            finish()
                            if(it.userInformationObj.userType == UserTypes.Student){
                                StudentMainActivity.startActivity(this@LoginActivity)
                            }else{
                                TeacherMainActivity.startActivity(this@LoginActivity)
                            }
                        })
                } else {
                    showOKAlert(
                        "Failed",
                        it.message,
                        onOkClick = { dialogInterface: DialogInterface, _: Int ->
                            dialogInterface.dismiss()
                        })
                }
            }
        }


        btnSignUp.setOnClickListener {
            finish()
            SignUpActivity.startActivity(this)
        }
    }
}