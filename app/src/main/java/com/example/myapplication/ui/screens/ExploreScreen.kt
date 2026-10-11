package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.local.entities.SkillType
import com.example.myapplication.data.local.entities.UserWithSkills
import com.example.myapplication.ui.viewmodel.SkillSwapViewModel
import com.example.myapplication.ui.viewmodel.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    viewModel: SkillSwapViewModel,
    modifier: Modifier = Modifier
) {
    val exploreState by viewModel.exploreState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val currentUserWithSkills by viewModel.currentUserWithSkills.collectAsState()

    var targetUserForSwap by remember { mutableStateOf<UserWithSkills?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.updateSearchQuery(it) },
                        placeholder = { Text("Search skills...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 16.dp),
                        singleLine = true,
                        shape = MaterialTheme.shapes.medium
                    )
                }
            )
        },
        modifier = modifier
    ) { padding ->
        when (val state = exploreState) {
            is UiState.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is UiState.Success -> {
                LazyColumn(
                    contentPadding = padding,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    items(state.data) { userWithSkills ->
                        UserCard(userWithSkills) {
                            targetUserForSwap = userWithSkills
                        }
                    }
                }
            }
            is UiState.Error -> {
                Text(text = "Error: ${state.message}", color = MaterialTheme.colorScheme.error)
            }
        }
    }

    if (targetUserForSwap != null) {
        val myOfferedSkills = currentUserWithSkills?.skills?.filter { it.type == SkillType.OFFERED } ?: emptyList()
        val theirOfferedSkills = targetUserForSwap?.skills?.filter { it.type == SkillType.OFFERED } ?: emptyList()

        var selectedMySkillId by remember { mutableStateOf(myOfferedSkills.firstOrNull()?.id ?: 0L) }
        var selectedTheirSkillId by remember { mutableStateOf(theirOfferedSkills.firstOrNull()?.id ?: 0L) }

        AlertDialog(
            onDismissRequest = { targetUserForSwap = null },
            title = { Text("Propose Skill Swap") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Proposing a swap with ${targetUserForSwap?.user?.name}")
                    
                    Text("Select a skill you will teach:", style = MaterialTheme.typography.labelLarge)
                    if (myOfferedSkills.isEmpty()) {
                        Text("You haven't listed any offered skills in your profile yet.", color = MaterialTheme.colorScheme.error)
                    } else {
                        myOfferedSkills.forEach { skill ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(
                                    selected = selectedMySkillId == skill.id,
                                    onClick = { selectedMySkillId = skill.id }
                                )
                                Text(skill.title, modifier = Modifier.padding(start = 8.dp))
                            }
                        }
                    }

                    Text("Select a skill you want to learn:", style = MaterialTheme.typography.labelLarge)
                    if (theirOfferedSkills.isEmpty()) {
                        Text("This user hasn't listed any skills to teach.", color = MaterialTheme.colorScheme.error)
                    } else {
                        theirOfferedSkills.forEach { skill ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(
                                    selected = selectedTheirSkillId == skill.id,
                                    onClick = { selectedTheirSkillId = skill.id }
                                )
                                Text(skill.title, modifier = Modifier.padding(start = 8.dp))
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    enabled = selectedMySkillId != 0L && selectedTheirSkillId != 0L,
                    onClick = {
                        viewModel.proposeSwap(
                            receiverId = targetUserForSwap!!.user.id,
                            offeredSkillId = selectedMySkillId,
                            requestedSkillId = selectedTheirSkillId
                        )
                        targetUserForSwap = null
                    }
                ) {
                    Text("Send Proposal")
                }
            },
            dismissButton = {
                TextButton(onClick = { targetUserForSwap = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun UserCard(
    userWithSkills: UserWithSkills,
    onProposeSwap: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(text = userWithSkills.user.name, style = MaterialTheme.typography.titleLarge)
            Text(text = userWithSkills.user.bio, style = MaterialTheme.typography.bodyMedium)
            
            Spacer(Modifier.height(8.dp))
            
            Text(text = "Teaching:", style = MaterialTheme.typography.labelLarge)
            SkillTags(userWithSkills.skills.filter { it.type == SkillType.OFFERED }, Color(0xFF4CAF50))
            
            Spacer(Modifier.height(4.dp))
            
            Text(text = "Learning:", style = MaterialTheme.typography.labelLarge)
            SkillTags(userWithSkills.skills.filter { it.type == SkillType.REQUESTED }, Color(0xFF2196F3))
            
            Spacer(Modifier.height(8.dp))
            
            Button(onClick = onProposeSwap, modifier = Modifier.align(Alignment.End)) {
                Text("Propose Swap")
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SkillTags(skills: List<com.example.myapplication.data.local.entities.Skill>, color: Color) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        skills.forEach { skill ->
            SuggestionChip(
                onClick = {},
                label = { Text(skill.title) },
                colors = SuggestionChipDefaults.suggestionChipColors(containerColor = color.copy(alpha = 0.1f))
            )
        }
    }
}
