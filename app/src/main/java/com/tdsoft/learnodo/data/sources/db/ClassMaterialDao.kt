package com.tdsoft.learnodo.data.sources.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tdsoft.learnodo.data.models.ClassMaterial
import com.tdsoft.learnodo.data.models.DBClassMaterial

@Dao
interface ClassMaterialDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun saveAll(listMaterials: List<DBClassMaterial>)

    @Query("SELECT * FROM DBClassMaterial")
    fun getAll(): List<DBClassMaterial>

    @Query("DELETE FROM DBClassMaterial")
    fun deleteAll()

    @Query("UPDATE DBClassMaterial SET localFile = :absolutePath WHERE materialId = :materialId")
    fun updateLocalFile(materialId: String?, absolutePath: String)
}