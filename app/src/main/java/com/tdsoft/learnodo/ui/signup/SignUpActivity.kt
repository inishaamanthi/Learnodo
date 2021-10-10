package com.tdsoft.learnodo.ui.signup

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import com.tdsoft.learnodo.R
import com.tdsoft.learnodo.base.BaseActivity
import com.tdsoft.learnodo.common.*
import com.tdsoft.learnodo.data.models.UserInformation
import com.tdsoft.learnodo.ui.login.LoginActivity
import com.tdsoft.learnodo.ui.student.StudentMainActivity
import com.tdsoft.learnodo.ui.teacher.TeacherMainActivity
import kotlinx.android.synthetic.main.activity_sign_up.*
import java.io.File

class SignUpActivity : BaseActivity() {
    companion object {
        fun startActivity(context: Context) {
            val intent = Intent(context, SignUpActivity::class.java)
            context.startActivity(intent)
        }
    }

    lateinit var userProfileImagePath: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_sign_up)
        initUI()
    }

    private fun initUI() {
        val items = listOf("Teacher", "Student")
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, items)
        val userType = (textInputUserType.editText as? AutoCompleteTextView)
        var userTypeValue = UserTypes.None
        userType!!.setOnItemClickListener { _, _, i, _ ->
            userTypeValue = if (i == 0) UserTypes.Teacher else UserTypes.Student
        }
        userType.setAdapter(adapter)

        btnLogin.setOnClickListener {
            finish()
            LoginActivity.startActivity(this)
        }

        btnRegister.setOnClickListener {

            val user = UserInformation(
                "",
                textInputName.editText!!.text.toString(),
                "",
                textInputAddress.editText!!.text.toString(),
                textInputEmail.editText!!.text.toString(),
                textInputPhone.editText!!.text.toString(),
                userTypeValue, null
            )

            if (textInputEmail.editText!!.text.isNullOrEmpty()) {
                textInputEmail.error = "Email cannot be empty"
                return@setOnClickListener
            }

            textInputEmail.error = null

            if (textInputName.editText!!.text.isNullOrEmpty()) {
                textInputName.error = "Name cannot be empty"
                return@setOnClickListener
            }
            textInputName.error = null

            if (textInputPhone.editText!!.text.isNullOrEmpty()) {
                textInputPhone.error = "Phone number cannot be empty"
                return@setOnClickListener
            }
            textInputPhone.error = null

            if (textInputAddress.editText!!.text.isNullOrEmpty()) {
                textInputAddress.error = "Address cannot be empty"
                return@setOnClickListener
            }
            textInputAddress.error = null

            if (textInputPassword.editText!!.text.isNullOrEmpty()) {
                textInputPassword.error = "Password cannot be empty"
                return@setOnClickListener
            }
            textInputPassword.error = null

            if (textInputUserType.editText!!.text.isNullOrEmpty()) {
                textInputUserType.error = "Select user type"
                return@setOnClickListener
            }
            textInputUserType.error = null
            progressBar2.show(btnRegister)
            callSignUp(user)

        }

        imgPhoto.setOnClickListener {
            requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

    }


    private fun callSignUp(user: UserInformation) {
        learnodoRepo
            .register(user, textInputPassword.editText!!.text.toString()) { it ->
                if (it.isSuccess) {
                    user.userId = it.userInformationObj!!.userId
                    if (::userProfileImagePath.isInitialized) {
                        learnodoRepo.uploadImage(
                            it.dbUser!!.uid.plus(".jpg"),
                            UploadImagesTypes.ProfileImage,
                            Uri.fromFile(File(userProfileImagePath)),
                            callBack = { isSuccess, url ->
                                progressBar2.hide(btnRegister)
                                if (isSuccess) {
                                    user.profileImage = url ?: ""
                                    learnodoRepo.saveCurrentUser(user)
                                    learnodoRepo.updateUserInfo(user) {
                                        if (!it.isSuccess) {
                                            showToast("Profile image uploading failed")
                                        }
                                    }
                                    registerForClass(user)
                                } else {
                                    learnodoRepo.saveCurrentUser(user)
                                    registerForClass(user)
                                    showToast("Profile image uploading failed")
                                }
                                showSignUpSuccessMessage(it.message, it.userInformationObj.userType)

                            }
                        )

                    } else {
                        progressBar2.hide(btnRegister)
                        learnodoRepo.registerForClass(it.userInformationObj) {

                        }
                        learnodoRepo.saveCurrentUser(it.userInformationObj)
                        showSignUpSuccessMessage(it.message, it.userInformationObj.userType)
                    }
                } else {
                    progressBar2.hide(btnRegister)
                    showOKAlert(
                        "Failed",
                        it.message,
                        onOkClick = { dialogInterface: DialogInterface, _: Int ->
                            dialogInterface.dismiss()
                        })
                }
            }
    }

    private fun showSignUpSuccessMessage(message: String, userTypes: Int) {
        showOKAlert(
            "Success",
            message,
            onOkClick = { dialogInterface: DialogInterface, _: Int ->
                dialogInterface.dismiss()
                if (userTypes == UserTypes.Student) {
                    StudentMainActivity.startActivity(this@SignUpActivity)
                } else {
                    TeacherMainActivity.startActivity(this@SignUpActivity)
                }

                finish()
            })
    }

    private fun registerForClass(student: UserInformation) {
        learnodoRepo.registerForClass(student) {

        }
    }


    override fun onPermissionGranted(granted: Boolean) {
        if (granted) {
            dispatchTakePictureIntent()
        } else {
            Toast.makeText(this, "Permission grant failed", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onImageCaptured(imagePath: String?) {
        if(imagePath!=null) {
            setPic(imgPhoto, imagePath)
            userProfileImagePath = imagePath
        }else{
            showOKAlert("Failed", "Image capturing failed") { dialogInterface: DialogInterface, i: Int ->
                dialogInterface.dismiss()
            }
        }
    }
}