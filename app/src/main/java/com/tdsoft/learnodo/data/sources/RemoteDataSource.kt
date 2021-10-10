package com.tdsoft.learnodo.data.sources

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.tdsoft.learnodo.common.*
import com.tdsoft.learnodo.data.IDataSource
import com.tdsoft.learnodo.data.models.*
import java.io.File


class RemoteDataSource : IDataSource {
    var database = FirebaseDatabase.getInstance().reference
    var auth = FirebaseAuth.getInstance()
    var storage = FirebaseStorage.getInstance().reference
    override fun register(
        userInformation: UserInformation,
        password: String,
        callBack: (CommonResponse) -> Unit
    ) {

        auth.createUserWithEmailAndPassword(userInformation.email!!, password)
            .addOnCompleteListener {

                var message = "Successfully registered"
                var userFb: FirebaseUser? = null
                if (it.isSuccessful) {
                    userFb = auth.currentUser
                    val userRef = database.child(FIREBASE_TABLE_USERS).child(userFb!!.uid)
                    userInformation.userId = userFb.uid
                    userRef.setValue(
                        userInformation
                    )
                } else {
                    message = it.exception?.message ?: "Something went wrong"
                }

                callBack(CommonResponse(it.isSuccessful, message, userFb, userInformation))
            }
    }

    override fun updateUserInfo(
        userInformation: UserInformation,
        callBack: (CommonResponse) -> Unit
    ) {
        val userFb = auth.currentUser
        val userRef = database.child(FIREBASE_TABLE_USERS).child(userFb!!.uid)
        userRef.child(FIREBASE_COLUMN_NAME).setValue(userInformation.name)
        userRef.child(FIREBASE_COLUMN_PROFILEIMAGE).setValue(userInformation.profileImage)
        userRef.child(FIREBASE_COLUMN_ADDRESS).setValue(userInformation.address)
        userRef.child(FIREBASE_COLUMN_USERTYPE).setValue(userInformation.userType)
    }

    override fun login(email: String, password: String, callBack: (CommonResponse) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                var message = "Login success"
                var userFb: FirebaseUser? = null
                if (it.isSuccessful) {
                    userFb = auth.currentUser
                    database.child(FIREBASE_TABLE_USERS).child(userFb!!.uid)
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val userObj = snapshot.getValue(UserInformation::class.java)
                                callBack(CommonResponse(it.isSuccessful, message, userFb, userObj))
                            }

                            override fun onCancelled(error: DatabaseError) {
                                message = "Something went wrong"
                                callBack(CommonResponse(it.isSuccessful, message, userFb))
                            }

                        })
                } else {
                    message = it.exception?.message ?: "Something went wrong"
                    callBack(CommonResponse(it.isSuccessful, message, userFb))
                }

            }
    }

    override fun logout() {
        auth.signOut()
    }

    override fun uploadImage(
        fileName: String?,
        @UploadImagesTypes imagesTypes: String, filUri: Uri,
        callBack: (Boolean, String?) -> Unit
    ) {
        storage.child("$imagesTypes/${fileName ?: filUri.lastPathSegment}").putFile(filUri)
            .addOnSuccessListener { it ->
                it.storage.downloadUrl.addOnSuccessListener {
                    callBack(true, it.toString())
                }.addOnFailureListener {
                    callBack(false, null)
                }

            }.addOnFailureListener {
                callBack(false, null)
            }
    }

    override fun registerForClass(
        userInformation: UserInformation,
        callBack: (ClassRoom?) -> Unit
    ) {
        val class1 = database.child(FIREBASE_TABLE_CLASSES).child("class1")
        var registeredPerson = class1
            .child(FIREBASE_TABLE_STUDENTS)
        if (userInformation.userType == UserTypes.Teacher) {
            registeredPerson = class1.child(FIREBASE_TABLE_TEACHERS)
        }
        val registeredPersonRef = registeredPerson.child(userInformation.userId!!)
        registeredPersonRef.setValue(
            userInformation
        )
        class1.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val registeredClass = snapshot.getValue(ClassRoom::class.java)
                callBack(registeredClass)
            }

            override fun onCancelled(error: DatabaseError) {
                callBack(null)
            }

        })
    }

    override fun getClassRoom(classId: String, callBack: (ClassRoom?) -> Unit) {
        val class1 = database.child(FIREBASE_TABLE_CLASSES).child(classId)
        class1.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val registeredClass = snapshot.getValue(ClassRoom::class.java)
                callBack(registeredClass)
            }

            override fun onCancelled(error: DatabaseError) {
                callBack(null)
            }

        })
    }

    override fun getStudents(classId: String, callBack: (List<UserInformation?>?) -> Unit) {
        val studentList = database.child(FIREBASE_TABLE_CLASSES).child(classId)
            .child(FIREBASE_TABLE_STUDENTS)
        studentList.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children.map { item ->
                    item.getValue(UserInformation::class.java)
                }
                callBack(list)
            }

            override fun onCancelled(error: DatabaseError) {
                callBack(null)
            }
        })
    }

    override fun getUserInformation(userId: String, callBack: (UserInformation?) -> Unit) {
        val user = database.child(FIREBASE_TABLE_USERS).child(userId)
        user.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userInformation = snapshot.getValue(UserInformation::class.java)
                callBack(userInformation)
            }

            override fun onCancelled(error: DatabaseError) {
                callBack(null)
            }

        })
    }

    override fun saveCurrentUser(userInformation: UserInformation) {
        TODO("Not yet implemented")
    }

    override fun getCurrentUser(): UserInformation {
        TODO("Not yet implemented")
    }

    override fun saveMaterials(listMaterials: List<DBClassMaterial>) {
        TODO("Not yet implemented")
    }

    override fun getAllMaterials(): List<DBClassMaterial> {
        TODO("Not yet implemented")
    }

    override fun deleteAllMaterials() {
        TODO("Not yet implemented")
    }

    override fun uploadMaterial(
        classMaterial: ClassMaterial, file: File,
        callBack: (ClassMaterial?) -> Unit
    ) {
        uploadImage(
            file.name,
            UploadImagesTypes.Materials,
            Uri.fromFile(file.absoluteFile),
            callBack = { isSuccess: Boolean, url: String? ->
                if (isSuccess) {
                    val newMaterial = database
                        .child(FIREBASE_TABLE_CLASSES)
                        .child(classMaterial.classId!!)
                        .child(FIREBASE_COLUMN_MATERIALS)
                        .push()
                    classMaterial.materialId = newMaterial.key
                    classMaterial.documentUrl = url
                    classMaterial.title = classMaterial.title ?: file.nameWithoutExtension
                    newMaterial.setValue(classMaterial)
                    callBack(classMaterial)
                } else {
                    callBack(null)
                }
            }
        )
    }

    override fun getTeachers(classId: String, callBack: (List<UserInformation?>?) -> Unit) {
        val teacherList = database.child(FIREBASE_TABLE_CLASSES).child(classId)
            .child(FIREBASE_TABLE_TEACHERS)
        teacherList.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children.map { item ->
                    item.getValue(UserInformation::class.java)
                }
                callBack(list)
            }

            override fun onCancelled(error: DatabaseError) {
                callBack(null)
            }
        })
    }

    override fun downloadFile(fileName: String, url: String, callBack: (file: File?) -> Unit) {
        val localFile = File.createTempFile(fileName, fileName.substring(fileName.lastIndexOf(".")))
        FirebaseStorage.getInstance().getReferenceFromUrl(url).getFile(localFile)
            .addOnSuccessListener {
                callBack(localFile)
            }.addOnFailureListener {
                callBack(null)
            }
    }

    override fun getMyExams(
        callBack: (marks: List<Exam>?) -> Unit
    ) {
        val examsRef = database.child(FIREBASE_TABLE_EXAMS)
        val listExams = ArrayList<Exam>()
        examsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val listExamFb = snapshot.children.toMutableList()
                    listExamFb.forEach {
                        val exam = it.getValue(Exam::class.java)!!
                        listExams.add(exam)
                    }
                    callBack(listExams)
                } else {
                    callBack(listExams)
                }
            }


            override fun onCancelled(error: DatabaseError) {
                callBack(null)
            }

        })
    }

    override fun getMarks(examId: String, userId: String, callBack: (marks: Marks?) -> Unit) {
        val marksRef = database.child(FIREBASE_TABLE_STUDENT_MARKS).child(examId).child(userId)
        marksRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val marks = snapshot.getValue(Marks::class.java)
                callBack(marks)
            }

            override fun onCancelled(error: DatabaseError) {
                callBack(null)
            }

        })
    }

    override fun enterMarks(marks: Marks, callBack: (isSuccess: Boolean) -> Unit) {
        val newMarks = database
            .child(FIREBASE_TABLE_STUDENT_MARKS)
            .child(marks.examId!!)
            .child(marks.studentId!!)
        newMarks.setValue(marks).addOnSuccessListener {
            callBack(true)
        }.addOnFailureListener {
            callBack(false)
        }
    }

    override fun getAllMarks(examId: String, callback: (marks: List<Marks>?) -> Unit) {
        val examsRef = database.child(FIREBASE_TABLE_STUDENT_MARKS)
            .child(examId)

        val listExams = ArrayList<Marks>()
        examsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val listExamFb = snapshot.children.toMutableList()
                    listExamFb.forEach {
                        val exam = it.getValue(Marks::class.java)!!
                        listExams.add(exam)
                    }
                    callback(listExams)
                } else {
                    callback(listExams)
                }
            }


            override fun onCancelled(error: DatabaseError) {
                callback(null)
            }

        })
    }

    override fun updateLocalMaterialFile(materialId: String?, absolutePath: String) {
        TODO("Not yet implemented")
    }

}