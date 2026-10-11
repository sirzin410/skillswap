#SkillSwap: Decentralized Knowledge & Skill Exchange Platform
## Comprehensive App Overview, Architecture, Code Documentation & Presentation Guide
 
---

## 1. Executive Summary
**SkillSwap** is a modern, peer-to-peer Android application designed to facilitate direct knowledge and skill exchange without monetary transactions. Users can list skills they can **teach** (Offered) and skills they want to **learn** (Requested), connect with other community members, propose skill-swapping sessions, and communicate in real-time via built-in chat.

---

## 2. Core Features & User Workflows
- **Profile & Skill Management:** Users maintain a personal profile with a bio and categorize their skills into Offered and Requested.
- **Explore & Community Search:** A searchable directory allowing users to discover other members and filter by skill keywords or categories.
- **Skill Swap Proposals:** Users can initiate a swap request by selecting one of their offered skills in exchange for a target user's offered skill.
- **In-App Messaging (Chat):** Direct messaging functionality between matched users to coordinate learning meetups.
- **Custom Adaptive App Icon:** Styled with modern Android adaptive icons supporting dynamic shaping and scaling.

---

## 3. Technical Architecture & Tech Stack
SkillSwap is built using modern Android development best practices recommended by Google:
- **UI Framework:** **Jetpack Compose** & **Material 3** for declarative, responsive UI components.
- **Architecture Pattern:** **MVVM (Model-View-ViewModel)** for clean separation of UI state, business logic, and data access.
- **Dependency Injection:** **Dagger Hilt** for compile-time dependency injection across ViewModels, Repositories, and Databases.
- **Local Persistence:** **Room Database** utilizing Kotlin Coroutines and reactive `Flow` streams for offline-first capabilities.

---

## 4. Code Deep Dive & Implementation

### A. Database Entity (`Skill.kt`)
Represents an individual skill offered or requested by a user.
```kotlin
@Entity(tableName = "skills")
data class Skill(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val userId: Long,
    val title: String,
    val category: String,
    val description: String,
    val type: SkillType // OFFERED or REQUESTED
)
```

### B. Database Access Object (`SkillSwapDao.kt`)
Handles Room database operations with reactive Flow returns.
```kotlin
@Dao
interface SkillSwapDao {
    @Transaction
    @Query("SELECT * FROM users")
    fun getAllUsersWithSkills(): Flow<List<UserWithSkills>>

    @Query("SELECT * FROM skills WHERE title LIKE '%' || :query || '%' OR category LIKE '%' || :query || '%'")
    fun searchSkills(query: String): Flow<List<Skill>>
}
```

### C. ViewModel (`SkillSwapViewModel.kt`)
Manages UI state and communicates with the repository using Coroutines and StateFlow.
```kotlin
@HiltViewModel
class SkillSwapViewModel @Inject constructor(
    private val repository: SkillSwapRepository
) : ViewModel() {

    val allUsersWithSkills: StateFlow<List<UserWithSkills>> =
        repository.getAllUsersWithSkills()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    fun insertSkill(skill: Skill) {
        viewModelScope.launch {
            repository.insertSkill(skill)
        }
    }
}
```

### D. UI Composable (`ExploreScreen.kt`)
Observes ViewModel state reactively and renders dynamic Compose lists.
```kotlin
@Composable
fun ExploreScreen(viewModel: SkillSwapViewModel = hiltViewModel()) {
    val users by viewModel.allUsersWithSkills.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        items(users) { userWithSkills ->
            UserCard(userWithSkills = userWithSkills) { selectedUser ->
                // Propose swap action
            }
        }
    }
}
```

---

## 5. Presentation Slide Deck Script

### Slide 1: Title Slide
- **Title:** SkillSwap: Decentralized Knowledge Exchange Platform
- **Subtitle:** Trade Skills, Not Money. Connect, Learn, and Grow Together.

### Slide 2: The Problem & Solution
- **Problem:** Formal courses and private tutors are expensive; people want to learn practical skills without financial barriers.
- **Solution:** SkillSwap enables direct peer-to-peer knowledge bartering.

### Slide 3: Key Features
- Profile & Skill Management (Offered vs. Requested)
- Community Explore & Keyword Search
- Swap Proposals & Real-Time Chat

### Slide 4: Tech Stack & Architecture
- Jetpack Compose & Material 3 (UI)
- MVVM + Dagger Hilt (Architecture & DI)
- Room Database & Kotlin Flow (Persistence & Reactivity)

### Slide 5: Conclusion & Future Scope
- Successful implementation of modern Android standards.
- Future roadmap: Cloud sync, Firebase Auth, and video call integration.