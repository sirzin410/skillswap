package com.example.myapplication.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.myapplication.data.local.entities.SkillType
import com.example.myapplication.ui.viewmodel.SkillSwapViewModel

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: SkillSwapViewModel,
    modifier: Modifier = Modifier
) {
    val currentUserWithSkills by viewModel.currentUserWithSkills.collectAsState()

    var showAddSkillDialog by remember { mutableStateOf(false) }
    var showEditBioDialog by remember { mutableStateOf(false) }

    var newSkillTitle by remember { mutableStateOf("") }
    var newSkillCategory by remember { mutableStateOf("") }
    var newSkillType by remember { mutableStateOf(SkillType.OFFERED) }

    var editBio by remember { mutableStateOf("") }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.updateProfilePicture(it.toString())
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "My Profile", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
            Button(
                onClick = { viewModel.logout() },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Logout")
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        currentUserWithSkills?.let { userWithSkills ->
            LaunchedEffect(userWithSkills) {
                editBio = userWithSkills.user.bio
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        if (userWithSkills.user.profilePicture.isNotBlank()) {
                            AsyncImage(
                                model = userWithSkills.user.profilePicture,
                                contentDescription = "Profile Picture",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Default Profile Picture",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(48.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = { galleryLauncher.launch("image/*") }
                    ) {
                        Text("Pick Picture from Gallery")
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(text = userWithSkills.user.name, style = MaterialTheme.typography.titleLarge)
                    Text(text = userWithSkills.user.email, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (userWithSkills.user.bio.isNotBlank()) userWithSkills.user.bio else "No bio added yet.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedButton(
                        onClick = {
                            editBio = userWithSkills.user.bio
                            showEditBioDialog = true
                        }
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Edit Bio")
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "My Skills", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                Button(onClick = { showAddSkillDialog = true }) {
                    Text("Add Skill")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "Skills I'm Offering:", style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(vertical = 4.dp))
            val offered = userWithSkills.skills.filter { it.type == SkillType.OFFERED }
            if (offered.isEmpty()) {
                Text("None listed yet.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.outline)
            } else {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    offered.forEach { skill ->
                        SuggestionChip(onClick = {}, label = { Text("${skill.title} (${skill.category})") })
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Skills I Want to Learn:", style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(vertical = 4.dp))
            val requested = userWithSkills.skills.filter { it.type == SkillType.REQUESTED }
            if (requested.isEmpty()) {
                Text("None listed yet.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.outline)
            } else {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    requested.forEach { skill ->
                        SuggestionChip(onClick = {}, label = { Text("${skill.title} (${skill.category})") })
                    }
                }
            }
        }
    }

    if (showEditBioDialog) {
        AlertDialog(
            onDismissRequest = { showEditBioDialog = false },
            title = { Text("Edit Bio") },
            text = {
                OutlinedTextField(
                    value = editBio,
                    onValueChange = { editBio = it },
                    label = { Text("Bio") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateBio(editBio)
                        showEditBioDialog = false
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditBioDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showAddSkillDialog) {
        AlertDialog(
            onDismissRequest = { showAddSkillDialog = false },
            title = { Text("Add New Skill") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = newSkillTitle,
                        onValueChange = { newSkillTitle = it },
                        label = { Text("Skill Title (e.g., Python)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newSkillCategory,
                        onValueChange = { newSkillCategory = it },
                        label = { Text("Category (e.g., Tech, Language)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text("Skill Type:", style = MaterialTheme.typography.labelLarge)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = newSkillType == SkillType.OFFERED,
                            onClick = { newSkillType = SkillType.OFFERED }
                        )
                        Text("Offering to teach", modifier = Modifier.padding(start = 8.dp))
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = newSkillType == SkillType.REQUESTED,
                            onClick = { newSkillType = SkillType.REQUESTED }
                        )
                        Text("Want to learn", modifier = Modifier.padding(start = 8.dp))
                    }
                }
            },
            confirmButton = {
                Button(
                    enabled = newSkillTitle.isNotBlank() && newSkillCategory.isNotBlank(),
                    onClick = {
                        viewModel.addSkill(newSkillTitle, newSkillCategory, newSkillType)
                        newSkillTitle = ""
                        newSkillCategory = ""
                        showAddSkillDialog = false
                    }
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddSkillDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
