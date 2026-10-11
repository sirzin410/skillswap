package com.example.myapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.local.entities.*
import com.example.myapplication.data.repository.SkillSwapRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class SkillSwapViewModel @Inject constructor(
    private val repository: SkillSwapRepository
) : ViewModel() {

    private val _currentUserId = MutableStateFlow<Long?>(null) // Starts as null for Login Flow
    val currentUserId = _currentUserId.asStateFlow()

    // Explore State
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    val exploreState: StateFlow<UiState<List<UserWithSkills>>> = combine(
        repository.getAllUsersWithSkills(),
        _searchQuery,
        _currentUserId
    ) { users, query, currentId ->
        if (currentId == null) {
            UiState.Success(emptyList())
        } else {
            val filtered = if (query.isEmpty()) {
                users
            } else {
                users.filter { userWithSkills ->
                    userWithSkills.skills.any { it.title.contains(query, ignoreCase = true) || it.category.contains(query, ignoreCase = true) }
                }
            }
            UiState.Success(filtered.filter { it.user.id != currentId })
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UiState.Loading)

    // All users state for name lookup in requests
    val allUsers: StateFlow<List<User>> = repository.getAllUsersWithSkills().map { list -> list.map { it.user } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Current User Profile State
    val currentUserWithSkills: StateFlow<UserWithSkills?> = _currentUserId.flatMapLatest { id ->
        if (id == null) flowOf(null)
        else repository.getAllUsersWithSkills().map { users -> users.find { it.user.id == id } }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Requests State
    val incomingRequests: StateFlow<List<SwapRequest>> = _currentUserId.flatMapLatest { id ->
        if (id == null) flowOf(emptyList()) else repository.getIncomingRequests(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val outgoingRequests: StateFlow<List<SwapRequest>> = _currentUserId.flatMapLatest { id ->
        if (id == null) flowOf(emptyList()) else repository.getOutgoingRequests(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Actions
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun login(email: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val allUsersWithSkills = repository.getAllUsersWithSkills().first()
            val existing = allUsersWithSkills.find { it.user.email.equals(email, ignoreCase = true) }
            if (existing != null) {
                _currentUserId.value = existing.user.id
                onResult(true, null)
            } else {
                onResult(false, "Account not found. Please create an account.")
            }
        }
    }

    fun signUp(name: String, email: String, bio: String, profilePicture: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val allUsersWithSkills = repository.getAllUsersWithSkills().first()
            val existing = allUsersWithSkills.find { it.user.email.equals(email, ignoreCase = true) }
            if (existing != null) {
                onResult(false, "Account already exists with this email. Please login.")
            } else {
                val newId = repository.insertUser(User(name = name, email = email, bio = bio.ifBlank { "Passionate about skill sharing!" }, profilePicture = profilePicture))
                _currentUserId.value = newId
                onResult(true, null)
            }
        }
    }

    fun updateBio(newBio: String) {
        val current = currentUserWithSkills.value?.user ?: return
        viewModelScope.launch {
            repository.updateUser(current.copy(bio = newBio))
        }
    }

    fun updateProfilePicture(newPicture: String) {
        val current = currentUserWithSkills.value?.user ?: return
        viewModelScope.launch {
            repository.updateUser(current.copy(profilePicture = newPicture))
        }
    }

    fun logout() {
        _currentUserId.value = null
    }

    fun addSkill(title: String, category: String, type: SkillType) {
        val userId = _currentUserId.value ?: return
        viewModelScope.launch {
            repository.insertSkill(Skill(userId = userId, title = title, category = category, type = type))
        }
    }

    fun proposeSwap(receiverId: Long, offeredSkillId: Long, requestedSkillId: Long) {
        viewModelScope.launch {
            repository.sendSwapRequest(
                senderId = _currentUserId.value ?: return@launch,
                receiverId = receiverId,
                offeredSkillId = offeredSkillId,
                requestedSkillId = requestedSkillId
            )
        }
    }

    fun acceptRequest(requestId: Long) {
        viewModelScope.launch {
            repository.updateRequestStatus(requestId, RequestStatus.ACCEPTED)
        }
    }

    fun declineRequest(requestId: Long) {
        viewModelScope.launch {
            repository.updateRequestStatus(requestId, RequestStatus.REJECTED)
        }
    }

    fun sendMessage(receiverId: Long, text: String) {
        val senderId = _currentUserId.value ?: return
        if (text.isBlank()) return
        viewModelScope.launch {
            repository.sendMessage(senderId, receiverId, text.trim())
        }
    }

    fun getMessagesForUser(otherUserId: Long): Flow<List<Message>> {
        val currentId = _currentUserId.value ?: return flowOf(emptyList())
        return repository.getMessagesBetweenUsers(currentId, otherUserId)
    }

    init {
        viewModelScope.launch {
            repository.getAllUsersWithSkills().first().let { users ->
                if (users.isEmpty()) {
                    val u3 = repository.insertUser(User(name = "Charlie Brown", email = "charlie@example.com", bio = "Public speaker and content creator."))
                    repository.insertSkill(Skill(userId = u3, title = "Public Speaking", category = "Communication", type = SkillType.OFFERED))
                    repository.insertSkill(Skill(userId = u3, title = "Python Programming", category = "Tech", type = SkillType.REQUESTED))
                }
            }
        }
    }
}
