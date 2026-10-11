# Implementation Plan - Gallery Image Picker & WhatsApp-Style Chat Feature

Add a gallery image picker for user profile pictures and an in-app WhatsApp-style chat feature for matched (accepted) swap requests.

## User Review Required

> [!IMPORTANT]
> - **Gallery Image Picker**: Replaces URL text input in profile editing with Android's system content picker (`ActivityResultContracts.GetContent()`) to select images from the gallery.
> - **In-App Chat**: When a swap request is accepted (matched), users can open a WhatsApp-style chat screen to exchange messages.
> - **Chat Database & UI**: New `Message` entity in Room database, repository methods, ViewModel state handling, and a WhatsApp-like chat UI (speech/message bubbles, input bar, auto-scroll).

## Proposed Changes

### Data & Room Database Layer
#### [NEW] [Message.kt](file:///C:/Users/Gasela/AndroidStudioProjects/MyApplication/app/src/main/java/com/example/myapplication/data/local/entities/Message.kt)
- Create `Message` entity (`messageId`, `senderId`, `receiverId`, `text`, `timestamp`).

#### [MODIFY] [SkillSwapDao.kt](file:///C:/Users/Gasela/AndroidStudioProjects/MyApplication/app/src/main/java/com/example/myapplication/data/local/SkillSwapDao.kt)
- Add methods for inserting messages and observing messages between two users (`getMessagesBetweenUsers(user1: Long, user2: Long)`).

#### [MODIFY] [SkillSwapDatabase.kt](file:///C:/Users/Gasela/AndroidStudioProjects/MyApplication/app/src/main/java/com/example/myapplication/data/local/SkillSwapDatabase.kt)
- Bump database version to `3` to include `Message` entity.

#### [MODIFY] [SkillSwapRepository.kt](file:///C:/Users/Gasela/AndroidStudioProjects/MyApplication/app/src/main/java/com/example/myapplication/data/repository/SkillSwapRepository.kt)
- Add methods for sending and observing messages.

### ViewModel Layer
#### [MODIFY] [SkillSwapViewModel.kt](file:///C:/Users/Gasela/AndroidStudioProjects/MyApplication/app/src/main/java/com/example/myapplication/ui/viewmodel/SkillSwapViewModel.kt)
- Add `sendMessage(receiverId: Long, text: String)` and `getMessagesForUser(otherUserId: Long)` flow.

### UI Screens & Navigation
#### [MODIFY] [ProfileScreen.kt](file:///C:/Users/Gasela/AndroidStudioProjects/MyApplication/app/src/main/java/com/example/myapplication/ui/screens/ProfileScreen.kt)
- Replace profile picture URL text field with a "Pick from Gallery" button using `rememberLauncherForActivityResult(ActivityResultContracts.GetContent())`.
- Display selected profile image URI if available (using Coil or AsyncImage / Android Image).

#### [MODIFY] [SwapRequestsScreen.kt](file:///C:/Users/Gasela/AndroidStudioProjects/MyApplication/app/src/main/java/com/example/myapplication/ui/screens/SwapRequestsScreen.kt)
- For accepted requests (matches), add a "Chat" button that navigates to the chat screen with that user.

#### [NEW] [ChatScreen.kt](file:///C:/Users/Gasela/AndroidStudioProjects/MyApplication/app/src/main/java/com/example/myapplication/ui/screens/ChatScreen.kt)
- WhatsApp-style chat UI featuring message bubbles (incoming vs outgoing), message input field, send button, and app bar with the other user's name/avatar.

#### [MODIFY] [MainActivity.kt](file:///C:/Users/Gasela/AndroidStudioProjects/MyApplication/app/src/main/java/com/example/myapplication/MainActivity.kt)
- Add navigation route for `chat/{otherUserId}` in `NavHost`.

## Verification Plan

### Automated Tests
- Build project successfully using Gradle (`gradle_build`).

### Manual Verification
- Deploy and verify:
  1. Profile picture selection using gallery picker.
  2. Accepting a swap request creates a match with a "Chat" button.
  3. Opening the chat page shows a WhatsApp-style interface where messages can be sent and received in real-time.
