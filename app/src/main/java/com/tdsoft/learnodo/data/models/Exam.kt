package com.tdsoft.learnodo.data.models

import android.os.Parcelable
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.android.parcel.Parcelize

@Parcelize
@IgnoreExtraProperties
data class Exam(
    var examId: String? = null,
    var examTitle: String? = null,
    var examDateTimeStamp: Long? = 0
) : Parcelable{
    override fun toString(): String {
        return examTitle!!
    }
}