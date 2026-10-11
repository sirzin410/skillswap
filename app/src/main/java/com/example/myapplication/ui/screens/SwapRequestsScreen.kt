package com.example.myapplication.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.myapplication.data.local.entities.RequestStatus
import com.example.myapplication.data.local.entities.SwapRequest
import com.example.myapplication.ui.viewmodel.SkillSwapViewModel

@Composable
fun SwapRequestsScreen(
    viewModel: SkillSwapViewModel,
    onNavigateToChat: (Long, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val incomingRequests by viewModel.incomingRequests.collectAsState()
    val outgoingRequests by viewModel.outgoingRequests.collectAsState()
    val allUsers by viewModel.allUsers.collectAsState()
    
    var selectedTab by remember { mutableStateOf(0) }

    val userMap = remember(allUsers) { allUsers.associateBy { id -> id.id } }

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Swap Requests & Matches", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(12.dp))

        TabRow(selectedTabIndex = selectedTab) {
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Incoming") })
            Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Outgoing") })
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (selectedTab == 0) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (incomingRequests.isEmpty()) {
                    item {
                        Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                            Text("No incoming swap requests yet.", color = MaterialTheme.colorScheme.outline)
                        }
                    }
                } else {
                    items(incomingRequests) { request ->
                        val sender = userMap[request.senderId]
                        val senderName = sender?.name ?: "User ${request.senderId}"
                        IncomingRequestItem(
                            senderName = senderName,
                            request = request,
                            onAccept = { viewModel.acceptRequest(request.requestId) },
                            onDecline = { viewModel.declineRequest(request.requestId) },
                            onChat = { onNavigateToChat(request.senderId, senderName) }
                        )
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (outgoingRequests.isEmpty()) {
                    item {
                        Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                            Text("No outgoing swap requests yet.", color = MaterialTheme.colorScheme.outline)
                        }
                    }
                } else {
                    items(outgoingRequests) { request ->
                        val receiver = userMap[request.receiverId]
                        val receiverName = receiver?.name ?: "User ${request.receiverId}"
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = "Request sent to $receiverName", style = MaterialTheme.typography.bodyLarge)
                                    StatusBadge(request.status)
                                }
                                if (request.status == RequestStatus.ACCEPTED) {
                                    Spacer(Modifier.height(8.dp))
                                    Button(
                                        onClick = { onNavigateToChat(request.receiverId, receiverName) },
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                    ) {
                                        Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(Modifier.width(8.dp))
                                        Text("Chat with $receiverName")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun IncomingRequestItem(
    senderName: String,
    request: SwapRequest,
    onAccept: () -> Unit,
    onDecline: () -> Unit,
    onChat: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(text = "Request from $senderName", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (request.status == RequestStatus.PENDING) {
                    Button(onClick = onAccept) { Text("Accept") }
                    OutlinedButton(onClick = onDecline) { Text("Decline") }
                } else {
                    StatusBadge(request.status)
                    if (request.status == RequestStatus.ACCEPTED) {
                        Spacer(Modifier.width(8.dp))
                        Button(onClick = onChat) {
                            Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Chat")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatusBadge(status: RequestStatus) {
    val color = when (status) {
        RequestStatus.PENDING -> MaterialTheme.colorScheme.primary
        RequestStatus.ACCEPTED -> Color(0xFF2E7D32)
        RequestStatus.REJECTED -> MaterialTheme.colorScheme.error
    }
    Surface(
        color = color.copy(alpha = 0.12f),
        contentColor = color,
        shape = MaterialTheme.shapes.extraSmall
    ) {
        Text(text = status.name, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall)
    }
}
