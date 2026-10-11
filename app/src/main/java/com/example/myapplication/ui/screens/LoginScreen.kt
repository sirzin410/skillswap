package com.example.myapplication.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.myapplication.ui.viewmodel.SkillSwapViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: SkillSwapViewModel,
    modifier: Modifier = Modifier
) {
    var isLoginMode by remember { mutableStateOf(true) }

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    var profilePicture by remember { mutableStateOf("") }
    
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var nameError by remember { mutableStateOf<String?>(null) }
    var generalError by remember { mutableStateOf<String?>(null) }

    var showVerificationDialog by remember { mutableStateOf(false) }
    var verificationCode by remember { mutableStateOf("") }
    var verificationError by remember { mutableStateOf<String?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            profilePicture = it.toString()
        }
    }

    Box(
        modifier = modifier.fillMaxSize().padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "SkillSwap",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = if (isLoginMode) "Welcome back! Please login to your account." else "Create a new account to connect & trade skills",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            generalError?.let {
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            if (!isLoginMode) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    if (profilePicture.isNotBlank()) {
                        AsyncImage(
                            model = profilePicture,
                            contentDescription = "Profile Picture Preview",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
                OutlinedButton(
                    onClick = { galleryLauncher.launch("image/*") }
                ) {
                    Text("Select Profile Picture from Gallery")
                }

                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        nameError = if (it.isBlank()) "Name cannot be empty" else null
                    },
                    label = { Text("Full Name") },
                    isError = nameError != null,
                    supportingText = { nameError?.let { Text(it) } },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    emailError = if (!it.contains("@") || it.length < 3) "Enter a valid email address" else null
                    generalError = null
                },
                label = { Text("Email Address") },
                isError = emailError != null,
                supportingText = { emailError?.let { Text(it) } },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    passwordError = if (it.length < 6) "Password must be at least 6 characters" else null
                },
                label = { Text("Password") },
                isError = passwordError != null,
                supportingText = { passwordError?.let { Text(it) } },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            if (!isLoginMode) {
                OutlinedTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    label = { Text("Bio (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val isEmailValid = email.contains("@") && email.length >= 3
                    val isPasswordValid = password.length >= 6
                    if (!isEmailValid) emailError = "Enter a valid email address"
                    if (!isPasswordValid) passwordError = "Password must be at least 6 characters"

                    if (isEmailValid && isPasswordValid) {
                        if (isLoginMode) {
                            viewModel.login(email) { success, error ->
                                if (success) {
                                    // logged in
                                } else {
                                    generalError = error
                                }
                            }
                        } else {
                            val isNameValid = name.isNotBlank()
                            if (!isNameValid) nameError = "Name cannot be empty"
                            else {
                                showVerificationDialog = true
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text(if (isLoginMode) "Login" else "Continue to Verification")
            }

            TextButton(
                onClick = {
                    isLoginMode = !isLoginMode
                    generalError = null
                    emailError = null
                    passwordError = null
                    nameError = null
                }
            ) {
                Text(if (isLoginMode) "Don't have an account? Create Account" else "Already have an account? Login")
            }
        }
    }

    if (showVerificationDialog) {
        AlertDialog(
            onDismissRequest = { showVerificationDialog = false },
            title = { Text("2-Step Verification") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Enter the 4-digit verification code sent to your email:")
                    OutlinedTextField(
                        value = verificationCode,
                        onValueChange = {
                            verificationCode = it
                            verificationError = null
                        },
                        label = { Text("Verification Code") },
                        isError = verificationError != null,
                        supportingText = { verificationError?.let { Text(it) } },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (verificationCode == "1234") {
                            showVerificationDialog = false
                            viewModel.signUp(name, email, bio, profilePicture) { success, error ->
                                if (!success) {
                                    generalError = error
                                }
                            }
                        } else {
                            verificationError = "Invalid verification code. Please check and try again."
                        }
                    }
                ) {
                    Text("Verify & Create Account")
                }
            },
            dismissButton = {
                TextButton(onClick = { showVerificationDialog = false }) {
                    Text("Cancel"
                    )
                }
            }
        )
    }
}
