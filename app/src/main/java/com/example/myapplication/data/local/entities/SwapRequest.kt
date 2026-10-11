package com.example.myapplication.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class RequestStatus {
    PENDING, ACCEPTED, REJECTED
}

@Entity(tableName = "swap_requests")
data class SwapRequest(
    @PrimaryKey(autoGenerate = true) val requestId: Long = 0,
    val senderId: Long,
    val receiverId: Long,
    val offeredSkillId: Long,
    val requestedSkillId: Long,
    val status: RequestStatus = RequestStatus.PENDING
)
