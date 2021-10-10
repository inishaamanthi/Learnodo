package com.tdsoft.learnodo.data.models

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class ClassMaterial(
    var materialId: String? = null,
    var documentUrl: String? = null,
    var title: String? = null,
    var description: String? = null,
    var classId:String? = null,
    var fileName:String? = null,
) {
    fun mapToDBModel(): DBClassMaterial {
        return DBClassMaterial(materialId!!, documentUrl, title, description)
    }
}