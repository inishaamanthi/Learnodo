package com.tdsoft.learnodo.data.models

import com.google.firebase.database.IgnoreExtraProperties


@IgnoreExtraProperties
data class ClassRoom(
    var classId: String? = null,
    var name: String? = null,
    var students: HashMap<String, UserInformation>? = null,
    var teachers: HashMap<String, UserInformation>? = null,
    var materials: HashMap<String, ClassMaterial>? = null
)
