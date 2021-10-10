package com.tdsoft.learnodo.data.sources.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.tdsoft.learnodo.data.models.ClassMaterial
import com.tdsoft.learnodo.data.models.DBClassMaterial

@Database(entities = [DBClassMaterial::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun classMaterialDao(): ClassMaterialDao
}