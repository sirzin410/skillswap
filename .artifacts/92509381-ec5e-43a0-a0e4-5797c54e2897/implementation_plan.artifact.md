# Implementation Plan - SkillSwap App

Create a native Android application called "SkillSwap" using Kotlin, Jetpack Compose, and Material 3, following Clean Architecture and MVVM.

## User Review Required

> [!IMPORTANT]
> The application will be built within the existing project structure (`com.example.myapplication`).
> I will add Hilt for Dependency Injection to follow modern best practices.

## Proposed Changes

### Build Configuration

#### [MODIFY] [libs.versions.toml](file:///C:/Users/Gasela/AndroidStudioProjects/MyApplication/gradle/libs.versions.toml)
- Add versions and libraries for Room, Hilt, Navigation Compose, and Coil.

#### [MODIFY] [build.gradle.kts (project)](file:///C:/Users/Gasela/AndroidStudioProjects/MyApplication/build.gradle.kts)
- Add Hilt and KSP plugins.

#### [MODIFY] [build.gradle.kts (app)](file:///C:/Users/Gasela/AndroidStudioProjects/MyApplication/app/build.gradle.kts)
- Apply Hilt and KSP plugins.
- Add dependencies for Room, Hilt, Navigation, and Coil.

---

### Data Layer (Room)

#### [NEW] [Entities](file:///C:/Users/Gasela/AndroidStudioProjects/MyApplication/app/src/main/java/com/example/myapplication/data/local/entities)
- `User.kt`: User entity.
- `Skill.kt`: Skill entity (offered/requested).
- `SwapRequest.kt`: Swap request entity.

#### [NEW] [SkillSwapDao.kt](file:///C:/Users/Gasela/AndroidStudioProjects/MyApplication/app/src/main/java/com/example/myapplication/data/local/SkillSwapDao.kt)
- Room DAO with Flow queries for search and status updates.

#### [NEW] [SkillSwapDatabase.kt](file:///C:/Users/Gasela/AndroidStudioProjects/MyApplication/app/src/main/java/com/example/myapplication/data/local/SkillSwapDatabase.kt)
- Room Database definition.

---

### Repository Layer

#### [NEW] [SkillSwapRepository.kt](file:///C:/Users/Gasela/AndroidStudioProjects/MyApplication/app/src/main/java/com/example/myapplication/data/repository/SkillSwapRepository.kt)
- Interface and implementation for data operations.

---

### UI & ViewModel Layer

#### [NEW] [SkillSwapViewModel.kt](file:///C:/Users/Gasela/AndroidStudioProjects/MyApplication/app/src/main/java/com/example/myapplication/ui/viewmodel/SkillSwapViewModel.kt)
- ViewModel using StateFlow to manage UI state (Explore, Profile, Requests).

#### [NEW] [ExploreScreen.kt](file:///C:/Users/Gasela/AndroidStudioProjects/MyApplication/app/src/main/java/com/example/myapplication/ui/screens/ExploreScreen.kt)
- User cards, search bar, and swap proposal logic.

#### [NEW] [ProfileScreen.kt](file:///C:/Users/Gasela/AndroidStudioProjects/MyApplication/app/src/main/java/com/example/myapplication/ui/screens/ProfileScreen.kt)
- View and edit profile/skills.

#### [NEW] [SwapRequestsScreen.kt](file:///C:/Users/Gasela/AndroidStudioProjects/MyApplication/app/src/main/java/com/example/myapplication/ui/screens/SwapRequestsScreen.kt)
- List requests with Accept/Decline actions.

#### [MODIFY] [MainActivity.kt](file:///C:/Users/Gasela/AndroidStudioProjects/MyApplication/app/src/main/java/com/example/myapplication/MainActivity.kt)
- Set up Navigation Compose `NavHost` and Hilt entry point.

---

### DI Setup

#### [NEW] [SkillSwapApp.kt](file:///C:/Users/Gasela/AndroidStudioProjects/MyApplication/app/src/main/java/com/example/myapplication/SkillSwapApp.kt)
- Application class with `@HiltAndroidApp`.

#### [NEW] [DatabaseModule.kt](file:///C:/Users/Gasela/AndroidStudioProjects/MyApplication/app/src/main/java/com/example/myapplication/di/DatabaseModule.kt)
- Hilt module for Room and Repository injection.

## Verification Plan

### Automated Tests
- I will verify the build by running `./gradlew assembleDebug`.
- (Optional) Room DAO unit tests if time permits.

### Manual Verification
- Deploy to device/emulator.
- Add skills in ProfileScreen.
- Search and propose swaps in ExploreScreen.
- Accept/Decline swaps in SwapRequestsScreen.
