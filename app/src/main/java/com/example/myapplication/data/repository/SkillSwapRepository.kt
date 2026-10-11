package com.example.myapplication.data.repository

import com.example.myapplication.data.local.SkillSwapDao
import com.example.myapplication.data.local.entities.*
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SkillSwapRepository @Inject constructor(
    private val dao: SkillSwapDao
) {
    // User
    suspend fun insertUser(user: User): Long = dao.insertUser(user)
    suspend fun updateUser(user: User) = dao.updateUser(user)
    fun getUserById(userId: Long): Flow<User?> = dao.getUserById(userId)
    fun getAllUsersWithSkills(): Flow<List<UserWithSkills>> = dao.getAllUsersWithSkills()

    // Skills
    suspend fun insertSkill(skill: Skill): Long = dao.insertSkill(skill)
    fun getSkillsForUser(userId: Long): Flow<List<Skill>> = dao.getSkillsForUser(userId)
    fun searchSkills(query: String): Flow<List<Skill>> = dao.searchSkills(query)

    // Swap Requests
    suspend fun sendSwapRequest(senderId: Long, receiverId: Long, offeredSkillId: Long, requestedSkillId: Long) {
        dao.insertSwapRequest(SwapRequest(
            senderId = senderId,
            receiverId = receiverId,
            offeredSkillId = offeredSkillId,
            requestedSkillId = requestedSkillId
        ))
    }

    suspend fun updateRequestStatus(requestId: Long, status: RequestStatus) {
        val request = dao.getRequestById(requestId)
        if (request != null) {
            dao.updateSwapRequest(request.copy(status = status))
        }
    }

    fun getIncomingRequests(userId: Long): Flow<List<SwapRequest>> = dao.getIncomingRequests(userId)
    fun getOutgoingRequests(userId: Long): Flow<List<SwapRequest>> = dao.getOutgoingRequests(userId)

    // Messages
    suspend fun sendMessage(senderId: Long, receiverId: Long, text: String) {
        dao.insertMessage(Message(senderId = senderId, receiverId = receiverId, text = text))
    }

    fun getMessagesBetweenUsers(user1: Long, user2: Long): Flow<List<Message>> = dao.getMessagesBetweenUsers(user1, user2)
}
