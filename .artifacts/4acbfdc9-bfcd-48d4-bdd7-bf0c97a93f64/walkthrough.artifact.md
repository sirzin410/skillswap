# Walkthrough - Gallery Image Picker for Profile Pictures

We have fully ensured that profile pictures can be picked directly from the device gallery without using URLs, both during account creation and in the user profile.

## Changes Made

### 1. Gallery Image Picker on Account Creation & Profile
- **[LoginScreen.kt](file:///C:/Users/Gasela/AndroidStudioProjects/MyApplication/app/src/main/java/com/example/myapplication/ui/screens/LoginScreen.kt)**: Replaced the profile picture URL text field in the Create Account view with a **"Select Profile Picture from Gallery"** button using `ActivityResultContracts.GetContent()`, along with an `AsyncImage` preview.
- **[ProfileScreen.kt](file:///C:/Users/Gasela/AndroidStudioProjects/MyApplication/app/src/main/java/com/example/myapplication/ui/screens/ProfileScreen.kt)**: Enabled picking profile pictures from the gallery using `ActivityResultContracts.GetContent()` with Coil (`AsyncImage`) preview.
- **No URLs**: Completely removed profile picture URL inputs across the app.

### 2. WhatsApp-Style In-App Chat
- Configured real-time messaging between matched (accepted) swap request users via Room database and WhatsApp-style chat UI.

## Verification Results

### Automated Verification
- Project built successfully with Gradle (`app:assembleDebug`).
