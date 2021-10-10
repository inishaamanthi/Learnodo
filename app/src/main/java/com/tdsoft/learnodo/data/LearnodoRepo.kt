package com.tdsoft.learnodo.data

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.net.Uri
import androidx.room.Room
import com.tdsoft.learnodo.common.CommonResponse
import com.tdsoft.learnodo.common.UploadImagesTypes
import com.tdsoft.learnodo.data.models.*
import com.tdsoft.learnodo.data.sources.LocalDataSource
import com.tdsoft.learnodo.data.sources.RemoteDataSource
import com.tdsoft.learnodo.data.sources.db.AppDatabase
import java.io.File

class LearnodoRepo private constructor(
    val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource
) : IDataSource {
    companion object {
        private var instance: LearnodoRepo? = null
        fun getInstance(context: Context) =
            if (instance != null) {
                instance
            } else {
                val db = Room.databaseBuilder(
                    context,
                    AppDatabase::class.java, "database-name"
                ).allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build()
                LearnodoRepo(
                    LocalDataSource(db,
                        context.getSharedPreferences(
                            "userDetails",
                            MODE_PRIVATE
                        )
                    ), RemoteDataSource()
                )
            }

    }

    override fun register(
        userInformation: UserInformation,
        password: String,
        callBack: (CommonResponse) -> Unit
    ) {
        remoteDataSource.register(userInformation, password, callBack)
    }

    override fun login(email: String, password: String, callBack: (CommonResponse) -> Unit) {
        remoteDataSource.login(email, password, callBack)
    }

    override fun logout() {
        remoteDataSource.logout()
    }

    override fun uploadImage(
        fileName: String?,
        @UploadImagesTypes imagesTypes: String,
        filUri: Uri,
        callBack: (Boolean, String?) -> Unit
    ) {
        remoteDataSource.uploadImage(fileName, imagesTypes, filUri, callBack)
    }

    override fun registerForClass(
        userInformation: UserInformation,
        callBack: (ClassRoom?) -> Unit
    ) {
        remoteDataSource.registerForClass(userInformation, callBack)
    }


    override fun updateUserInfo(
        userInformation: UserInformation,
        callBack: (CommonResponse) -> Unit
    ) {
        remoteDataSource.updateUserInfo(userInformation, callBack)
    }

    override fun getClassRoom(classId: String, callBack: (ClassRoom?) -> Unit) {
        remoteDataSource.getClassRoom(classId, callBack)
    }


    override fun getUserInformation(userId: String, callBack: (UserInformation?) -> Unit) {
        remoteDataSource.getUserInformation(userId, callBack)
    }

    override fun saveCurrentUser(userInformation: UserInformation) {
        localDataSource.saveCurrentUser(userInformation)
    }

    override fun getCurrentUser() = localDataSource.getCurrentUser()
    override fun saveMaterials(listMaterials: List<DBClassMaterial>) {
        localDataSource.saveMaterials(listMaterials)
    }

    override fun getAllMaterials(): List<DBClassMaterial> {
        return localDataSource.getAllMaterials()
    }

    override fun deleteAllMaterials() {
        localDataSource.deleteAllMaterials()
    }

    override fun uploadMaterial( classMaterial: ClassMaterial, file: File,
                                 callBack: (ClassMaterial?) -> Unit) {
        remoteDataSource.uploadMaterial(classMaterial, file, callBack)
    }

    override fun getStudents(classId: String, callBack: (List<UserInformation?>?) -> Unit) {
        remoteDataSource.getStudents(classId, callBack)
    }

    override fun getTeachers(classId: String, callBack: (List<UserInformation?>?) -> Unit) {
        remoteDataSource.getTeachers(classId, callBack)
    }

    override fun downloadFile(fileName: String, url: String, callBack: (file: File?) -> Unit) {
        remoteDataSource.downloadFile(fileName, url, callBack)
    }

    override fun getMyExams(
        callBack: (marks: List<Exam>?) -> Unit
    ) {
        remoteDataSource.getMyExams(callBack)
    }

    override fun getMarks(examId: String, userId: String, callBack: (marks: Marks?) -> Unit) {
        remoteDataSource.getMarks(examId, userId, callBack)
    }

    override fun enterMarks(marks: Marks, callBack: (isSuccess: Boolean) -> Unit) {
        remoteDataSource.enterMarks(marks, callBack)
    }

    override fun getAllMarks(examId: String, callback: (marks: List<Marks>?) -> Unit) {
        remoteDataSource.getAllMarks(examId, callback)
    }

    override fun updateLocalMaterialFile(materialId: String?, absolutePath: String) {
        localDataSource.updateLocalMaterialFile(materialId, absolutePath)
    }

}