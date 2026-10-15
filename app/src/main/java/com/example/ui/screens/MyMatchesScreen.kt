package com.example.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Tournament
import com.example.ui.theme.DarkBlueAccent
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkError
import com.example.ui.theme.DarkPrimaryGreen
import com.example.ui.theme.DarkWarning
import com.example.ui.viewmodel.EventKashifViewModel

@Composable
fun MyMatchesScreen(
    viewModel: EventKashifViewModel,
    onViewRoomClick: (Tournament) -> Unit,
    onSubmitPaymentClick: (Tournament) -> Unit,
    onExploreClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val registrations by viewModel.userRegistrations.collectAsStateWithLifecycle()
    val allTournaments by viewModel.allTournaments.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()

    if (currentUser == null) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.SportsEsports,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(56.dp)
                )
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "Sign in to View Your Matches",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Track your registered tournaments, payment reviews, and secret room passwords.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        return
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "My Registered Tournaments (${registrations.size})",
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        if (registrations.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.SportsEsports,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(52.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No Registered Matches Yet",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Join Battle Royale or Clash Squad cups to compete for real cash prizes!",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onExploreClick,
                            colors = ButtonDefaults.buttonColors(containerColor = DarkPrimaryGreen, contentColor = Color.Black)
                        ) {
                            Text("Browse Tournaments", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        } else {
            items(registrations, key = { it.id }) { reg ->
                val tournament = allTournaments.firstOrNull { it.id == reg.tournamentId }
                val isConfirmed = reg.registrationStatus == "CONFIRMED"
                val isUnderReview = reg.paymentStatus == "UNDER_REVIEW"
                val isPendingClaim = reg.paymentStatus == "PENDING_CLAIM"

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(
                        width = if (isConfirmed) 1.5.dp else 1.dp,
                        color = if (isConfirmed) DarkPrimaryGreen else DarkBorder
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("registered_match_card_${reg.id}")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = reg.tournamentTitle.ifBlank { tournament?.title ?: "Tournament" },
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "In-Game ID: ${reg.inGameId} • Team: ${reg.teamName}",
                                    fontSize = 11.sp,
                                    color = DarkBlueAccent
                                )
                            }

                            // Status Tag
                            when {
                                isConfirmed -> {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = DarkPrimaryGreen.copy(alpha = 0.2f)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = DarkPrimaryGreen, modifier = Modifier.size(12.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("CONFIRMED", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = DarkPrimaryGreen)
                                        }
                                    }
                                }
                                isUnderReview -> {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = DarkWarning.copy(alpha = 0.2f)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(Icons.Default.HourglassTop, contentDescription = null, tint = DarkWarning, modifier = Modifier.size(12.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("UNDER REVIEW", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = DarkWarning)
                                        }
                                    }
                                }
                                reg.paymentStatus == "REJECTED" -> {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = DarkError.copy(alpha = 0.2f)
                                    ) {
                                        Text("PAYMENT REJECTED", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = DarkError, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                                    }
                                }
                                else -> {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant
                                    ) {
                                        Text("PENDING PAYMENT", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        if (tournament != null) {
                            Text(
                                text = "Start Time: ${tournament.startAt} • Mode: ${tournament.gameMode}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        if (reg.transactionReference.isNotBlank()) {
                            Text(
                                text = "Submitted UTR: ${reg.transactionReference}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Action button
                        when {
                            isConfirmed && tournament != null -> {
                                Button(
                                    onClick = { onViewRoomClick(tournament) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = DarkPrimaryGreen,
                                        contentColor = Color.Black
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(Icons.Default.LockOpen, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("View Room ID & Password", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                            }
                            isPendingClaim && tournament != null -> {
                                Button(
                                    onClick = { onSubmitPaymentClick(tournament) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = DarkPrimaryGreen,
                                        contentColor = Color.Black
                                    ),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Submit Payment UTR Reference", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                            }
                            isUnderReview -> {
                                Text(
                                    text = "Admin is verifying your UTR reference against bank records. Room ID will be unlocked once approved.",
                                    fontSize = 11.sp,
                                    color = DarkWarning,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
