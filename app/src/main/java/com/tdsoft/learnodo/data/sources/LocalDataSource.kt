package com.tdsoft.learnodo.data.sources

import android.content.SharedPreferences
import android.net.Uri
import com.tdsoft.learnodo.common.CommonResponse
import com.tdsoft.learnodo.common.PREF_CUR_USER
import com.tdsoft.learnodo.common.convertToJson
import com.tdsoft.learnodo.common.convertToObject
import com.tdsoft.learnodo.data.IDataSource
import com.tdsoft.learnodo.data.models.*
import com.tdsoft.learnodo.data.sources.db.AppDatabase
import java.io.File

class LocalDataSource(
    private val appDatabase: AppDatabase,
    private val preferences: SharedPreferences
) : IDataSource {
    override fun register(
        userInformation: UserInformation,
        password: String,
        callBack: (CommonResponse) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun login(email: String, password: String, callBack: (CommonResponse) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun logout() {
        TODO("Not yet implemented")
    }

    override fun uploadImage(
        fileName: String?,
        imagesTypes: String,
        filUri: Uri,
        callBack: (Boolean, String?) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun registerForClass(
        userInformation: UserInformation,
        callBack: (ClassRoom?) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun updateUserInfo(
        userInformation: UserInformation,
        callBack: (CommonResponse) -> Unit
    ) {
        TODO("Not yet implemented")
    }


    override fun getClassRoom(classId: String, callBack: (ClassRoom?) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun getUserInformation(userId: String, callBack: (UserInformation?) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun saveCurrentUser(userInformation: UserInformation) {
        preferences.edit().putString(PREF_CUR_USER, userInformation.convertToJson()).apply()
    }

    override fun getCurrentUser(): UserInformation? {
        val userJson = preferences.getString(PREF_CUR_USER, null)

        if (!userJson.isNullOrEmpty()) {
            return userJson.convertToObject()
        }
        return null
    }

    override fun saveMaterials(listMaterials: List<DBClassMaterial>) {
        appDatabase.classMaterialDao().saveAll(listMaterials)
    }

    override fun getAllMaterials() = appDatabase.classMaterialDao().getAll()

    override fun deleteAllMaterials() {
        appDatabase.classMaterialDao().deleteAll()
    }

    override fun uploadMaterial(
        classMaterial: ClassMaterial, file: File,
        callBack: (ClassMaterial?) -> Unit
    ) {
        TODO("Not yet implemented")
    }


    override fun getStudents(classId: String, callBack: (List<UserInformation?>?) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun getTeachers(classId: String, callBack: (List<UserInformation?>?) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun downloadFile(fileName: String, url: String, callBack: (file: File?) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun getMyExams(
        callBack: (marks: List<Exam>?) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun getMarks(examId: String, userId: String, callBack: (marks: Marks?) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun enterMarks(marks: Marks, callBack: (isSuccess: Boolean) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun getAllMarks(examId: String, callback: (marks: List<Marks>?) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun updateLocalMaterialFile(materialId: String?, absolutePath: String) {
        appDatabase.classMaterialDao().updateLocalFile(materialId, absolutePath)
    }

}