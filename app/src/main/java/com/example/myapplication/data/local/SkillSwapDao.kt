package com.example.myapplication.data.local

import androidx.room.*
import com.example.myapplication.data.local.entities.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SkillSwapDao {
    // User
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User): Long

    @Update
    suspend fun updateUser(user: User)

    @Query("SELECT * FROM users WHERE id = :userId")
    fun getUserById(userId: Long): Flow<User?>

    @Transaction
    @Query("SELECT * FROM users")
    fun getAllUsersWithSkills(): Flow<List<UserWithSkills>>

    // Skills
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSkill(skill: Skill): Long

    @Query("SELECT * FROM skills WHERE userId = :userId")
    fun getSkillsForUser(userId: Long): Flow<List<Skill>>

    @Query("SELECT * FROM skills WHERE title LIKE '%' || :query || '%' OR category LIKE '%' || :query || '%'")
    fun searchSkills(query: String): Flow<List<Skill>>

    // Swap Requests
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSwapRequest(request: SwapRequest)

    @Update
    suspend fun updateSwapRequest(request: SwapRequest)

    @Query("SELECT * FROM swap_requests WHERE receiverId = :userId")
    fun getIncomingRequests(userId: Long): Flow<List<SwapRequest>>

    @Query("SELECT * FROM swap_requests WHERE senderId = :userId")
    fun getOutgoingRequests(userId: Long): Flow<List<SwapRequest>>

    @Query("SELECT * FROM swap_requests WHERE requestId = :id")
    suspend fun getRequestById(id: Long): SwapRequest?

    // Messages
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: Message)

    @Query("SELECT * FROM messages WHERE (senderId = :user1 AND receiverId = :user2) OR (senderId = :user2 AND receiverId = :user1) ORDER BY timestamp ASC")
    fun getMessagesBetweenUsers(user1: Long, user2: Long): Flow<List<Message>>
}
