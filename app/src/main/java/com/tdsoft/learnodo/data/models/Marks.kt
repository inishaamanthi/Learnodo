package com.tdsoft.learnodo.data.models

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Marks(
    var examId: String? = null,
    var studentId: String? = null,
    var studentName: String? = null,
    var marks: Int? = 0,
    var examTitle: String? = null
)