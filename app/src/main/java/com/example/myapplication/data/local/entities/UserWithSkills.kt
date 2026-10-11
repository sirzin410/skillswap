package com.example.myapplication.data.local.entities

import androidx.room.Embedded
import androidx.room.Relation

data class UserWithSkills(
    @Embedded val user: User,
    @Relation(
        parentColumn = "id",
        entityColumn = "userId"
    )
    val skills: List<Skill>
)
