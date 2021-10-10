package com.tdsoft.learnodo.ui.splash

import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.google.firebase.auth.FirebaseAuth
import com.tdsoft.learnodo.R
import com.tdsoft.learnodo.base.BaseActivity
import com.tdsoft.learnodo.common.UserTypes
import com.tdsoft.learnodo.common.isInternetAvailable
import com.tdsoft.learnodo.ui.login.LoginActivity
import com.tdsoft.learnodo.ui.student.StudentMainActivity
import com.tdsoft.learnodo.ui.teacher.TeacherMainActivity

class SplashActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        if (!isInternetAvailable()) {
            showOKAlert(
                "Warning",
                "No internet connection"
            ) { dialogInterface: DialogInterface, _: Int ->
                dialogInterface.dismiss()
                if (learnodoRepo.getCurrentUser() != null) {
                    if (learnodoRepo.getCurrentUser()!!.userType == UserTypes.Student) {
                        StudentMainActivity.startActivity(this@SplashActivity)
                    }
                } else {
                    finish()
                }
            }
            return
        }

        Handler(Looper.getMainLooper()).postDelayed({
            if (FirebaseAuth.getInstance().currentUser != null) {
                with(FirebaseAuth.getInstance().currentUser) {
                    learnodoRepo.getUserInformation(this?.uid!!) {
                        if (it != null) {
                            if (it.userType == UserTypes.Student) {
                                StudentMainActivity.startActivity(this@SplashActivity)
                            } else {
                                TeacherMainActivity.startActivity(this@SplashActivity)
                            }
                        } else {
                            LoginActivity.startActivity(this@SplashActivity)
                        }
                    }
                }

            } else {
                LoginActivity.startActivity(this)
            }
            finish()

        }, 3000)
    }

}