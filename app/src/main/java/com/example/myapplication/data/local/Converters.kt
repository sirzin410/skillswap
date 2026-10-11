package com.example.myapplication.data.local

import androidx.room.TypeConverter
import com.example.myapplication.data.local.entities.RequestStatus
import com.example.myapplication.data.local.entities.SkillType

class Converters {
    @TypeConverter
    fun fromSkillType(value: SkillType): String = value.name

    @TypeConverter
    fun toSkillType(value: String): SkillType = SkillType.valueOf(value)

    @TypeConverter
    fun fromRequestStatus(value: RequestStatus): String = value.name

    @TypeConverter
    fun toRequestStatus(value: String): RequestStatus = RequestStatus.valueOf(value)
}
