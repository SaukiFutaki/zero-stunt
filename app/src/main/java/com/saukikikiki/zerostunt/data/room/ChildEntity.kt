package com.saukikikiki.zerostunt.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "child_data")
data class ChildEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val gender: Float,
    val age: Float,
    val birthWeight: Float,
    val birthLength: Float,
    val bodyWeight: Float,
    val bodyLength: Float,
    val stuntingStatus: String
)
