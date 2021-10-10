package com.tdsoft.learnodo.data.models

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class DBClassMaterial(
    @PrimaryKey @NonNull var materialId: String,
    var documentUrl: String? = null,
    var title: String? = null,
    var description: String? = null,
    var localFile: String? = null
) {
    fun mapToFbModel(): ClassMaterial {
        return ClassMaterial(materialId, localFile, title, description)
    }
}