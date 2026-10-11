package com.example.myapplication.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class SkillType {
    OFFERED, REQUESTED
}

@Entity(tableName = "skills")
data class Skill(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val title: String,
    val category: String,
    val type: SkillType
)
