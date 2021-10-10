package com.tdsoft.learnodo.data

import android.net.Uri
import com.tdsoft.learnodo.common.CommonResponse
import com.tdsoft.learnodo.common.UploadImagesTypes
import com.tdsoft.learnodo.data.models.*
import java.io.File

interface IDataSource {
    fun register(
        userInformation: UserInformation,
        password: String,
        callBack: (CommonResponse) -> Unit
    )

    fun updateUserInfo(userInformation: UserInformation, callBack: (CommonResponse) -> Unit)
    fun login(email: String, password: String, callBack: (CommonResponse) -> Unit)
    fun logout()
    fun uploadImage(
        fileName: String? = null, @UploadImagesTypes imagesTypes: String, filUri: Uri,
        callBack: (Boolean, String?) -> Unit,
    )

    fun registerForClass(userInformation: UserInformation, callBack: (ClassRoom?) -> Unit)
    fun getClassRoom(classId: String, callBack: (ClassRoom?) -> Unit)
    fun getStudents(classId: String, callBack: (List<UserInformation?>?) -> Unit)
    fun getTeachers(classId: String, callBack: (List<UserInformation?>?) -> Unit)
    fun getUserInformation(userId: String, callBack: (UserInformation?) -> Unit)
    fun saveCurrentUser(userInformation: UserInformation)
    fun getCurrentUser(): UserInformation?
    fun saveMaterials(listMaterials: List<DBClassMaterial>)
    fun getAllMaterials(): List<DBClassMaterial>
    fun deleteAllMaterials()
    fun uploadMaterial(
        classMaterial: ClassMaterial, file: File,
        callBack: (ClassMaterial?) -> Unit
    )

    fun downloadFile(fileName: String, url: String, callBack: (file: File?) -> Unit)
    fun getMyExams(callBack: (marks: List<Exam>?) -> Unit)
    fun getMarks(examId: String, userId: String, callBack: (marks: Marks?) -> Unit)
    fun enterMarks(marks: Marks, callBack: (isSuccess: Boolean) -> Unit)
    fun getAllMarks(examId: String, callback: (marks: List<Marks>?) -> Unit)
    fun updateLocalMaterialFile(materialId: String?, absolutePath: String)
}