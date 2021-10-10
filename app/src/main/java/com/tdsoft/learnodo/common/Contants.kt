package com.tdsoft.learnodo.common

annotation class UserTypes{
    companion object{
        const val None = -1
        const val Teacher = 0
        const val Student = 1
    }
}

annotation class UploadImagesTypes{
    companion object{
        const val ProfileImage = "profileimages"
        const val Materials = "materials"
    }
}

const val FIREBASE_TABLE_USERS = "users"
const val FIREBASE_TABLE_CLASSES = "classes"
const val FIREBASE_TABLE_EXAMS = "exams"
const val FIREBASE_TABLE_STUDENTS = "students"
const val FIREBASE_TABLE_STUDENT_MARKS = "studentMarks"
const val FIREBASE_TABLE_TEACHERS = "teachers"
const val FIREBASE_COLUMN_MATERIALS = "materials"
const val FIREBASE_COLUMN_NAME = "name"
const val FIREBASE_COLUMN_PROFILEIMAGE = "profileImage"
const val FIREBASE_COLUMN_ADDRESS = "address"
const val FIREBASE_COLUMN_USERTYPE = "userType"
const val CLASS_ID = "class1"


const val PREF_CUR_USER = "curUSer"

const val CHANNEL_ID = "LearnodoNotification"