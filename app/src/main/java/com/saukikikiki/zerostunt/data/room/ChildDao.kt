package com.saukikikiki.zerostunt.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update


@Dao
interface ChildDataDao {
    @Insert
    suspend fun insert(childData: ChildEntity)

    @Update
    suspend fun update(childData: ChildEntity)

    @Query("SELECT * FROM child_data")
    fun getAllChildData(): List<ChildEntity>

    @Query("SELECT * FROM child_data ORDER BY id DESC LIMIT 1")
    fun getLastChildData(): ChildEntity?
}