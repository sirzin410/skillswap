package com.example.myapplication.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.myapplication.data.local.entities.Message
import com.example.myapplication.data.local.entities.Skill
import com.example.myapplication.data.local.entities.SwapRequest
import com.example.myapplication.data.local.entities.User

@Database(entities = [User::class, Skill::class, SwapRequest::class, Message::class], version = 3, exportSchema = false)
@TypeConverters(Converters::class)
abstract class SkillSwapDatabase : RoomDatabase() {
    abstract fun skillSwapDao(): SkillSwapDao
}
